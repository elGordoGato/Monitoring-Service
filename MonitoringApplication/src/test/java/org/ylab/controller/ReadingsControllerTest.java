package org.ylab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.ylab.authentication.SecurityConfig;
import org.ylab.domain.dto.ReadingDto;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.enums.Role;
import org.ylab.domain.mapper.ReadingMapper;
import org.ylab.usecase.service.MeterService;
import org.ylab.usecase.service.ReadingService;
import org.ylab.usecase.service.UserService;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test class for Reading Controller")
@WebMvcTest(ReadingsController.class)
@Import(SecurityConfig.class)
class ReadingsControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    @Qualifier("userReadingService")
    private ReadingService readingService;
    @MockBean
    private MeterService meterService;
    @MockBean
    private UserService userService;
    @MockBean
    private ReadingMapper readingMapper;
    @Autowired
    private MockMvc mockMvc;
    private UserEntity principal;
    private Authentication authentication;
    private Meter meter;
    private List<ReadingDto> readingDtos;
    private List<Reading> readings;

    @BeforeEach
    void setUp() {
        principal = new UserEntity(1, "user@example.com",
                "FirstName", "LastName",
                "password", Role.USER);
        UserDetails userDetails = User.builder()
                .username(principal.getEmail())
                .password(principal.getPassword())
                .roles(principal.getRole().toString())
                .build();
        authentication = new TestingAuthenticationToken(
                principal, principal.getPassword(), userDetails.getAuthorities());
        when(userService.authenticate(anyString(), anyString())).thenReturn(principal);
        meter = new Meter((short) 1, "Some meter");
        readings = List.of(
                new Reading(1L, principal, meter, 123L, Instant.now()),
                new Reading(2L, principal, meter, 654L, Instant.now())
        );
        readingDtos = List.of(
                new ReadingDto((short) 1, 123L, "2024-1-1"),
                new ReadingDto((short) 1, 654L, "2024-2-5")
        );
    }

    @Test
    @DisplayName("Test GET '/readings' endpoint, should return list of actual readings")
    void getActual_shouldReturnReadings() throws Exception {
        // given
        when(readingService.getActual(any(principal.getClass()))).thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/readings")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].collectedDate", is("2024-1-1")))
                .andExpect(jsonPath("$[0].reading", is(123)))
                .andExpect(jsonPath("$[0].meterType", is(1)))
                .andExpect(jsonPath("$[1].collectedDate", is("2024-2-5")))
                .andExpect(jsonPath("$[1].reading", is(654)))
                .andExpect(jsonPath("$[1].meterType", is(1)));
    }

    @Test
    @DisplayName("Test GET '/readings' endpoint with YearMonth parameter, " +
            "should return list of readings submitted within selected month")
    void getByMonth_shouldReturnReadingsForGivenMonth() throws Exception {
        // given
        YearMonth date = YearMonth.of(2024, 1);
        when(readingService.getForMonth(any(principal.getClass()), ArgumentMatchers.eq(date.atDay(1))))
                .thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/readings")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                .param("date", date.toString()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].collectedDate", is("2024-1-1")))
                .andExpect(jsonPath("$[0].reading", is(123)))
                .andExpect(jsonPath("$[0].meterType", is(1)))
                .andExpect(jsonPath("$[1].collectedDate", is("2024-2-5")))
                .andExpect(jsonPath("$[1].reading", is(654)))
                .andExpect(jsonPath("$[1].meterType", is(1)));
    }

    @Test
    @DisplayName("Test GET '/readings/history' endpoint, " +
            "should return list of all readings submitted by principal user")
    void getHistory_shouldReturnAllReadingsForUser() throws Exception {
        // given
        when(readingService.getAllByUser(any(principal.getClass()))).thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/readings/history")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].collectedDate", is("2024-1-1")))
                .andExpect(jsonPath("$[0].reading", is(123)))
                .andExpect(jsonPath("$[0].meterType", is(1)))
                .andExpect(jsonPath("$[1].collectedDate", is("2024-2-5")))
                .andExpect(jsonPath("$[1].reading", is(654)))
                .andExpect(jsonPath("$[1].meterType", is(1)));
    }

    @Test
    @DisplayName("Test of POST /readings endpoint with body of ReadingDto json, " +
            "should return created json of created reading and status 201")
    void createReadings_shouldReturnCreatedReading() throws Exception {
        // given
        ReadingDto inputReadingDto = ReadingDto.builder().meterType((short) 1).reading(789L).build();
        Reading createdReading = new Reading(3L, principal, meter, 789L, Instant.now());
        ReadingDto expectedReadingDto = new ReadingDto((short) 1, 789L, "2024-3-10");
        when(meterService.getById(inputReadingDto.getMeterType())).thenReturn(meter);
        when(readingService.create(
                any(principal.getClass()),
                ArgumentMatchers.eq(meter),
                ArgumentMatchers.eq(inputReadingDto.getReading())))
                .thenReturn(createdReading);
        when(readingMapper.toReadingDto(createdReading)).thenReturn(expectedReadingDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/readings")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputReadingDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.collectedDate", is("2024-3-10")))
                .andExpect(jsonPath("$.reading", is(789)))
                .andExpect(jsonPath("$.meterType", is(1)));
    }
}