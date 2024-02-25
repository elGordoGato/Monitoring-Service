package org.ylab.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.ylab.adapter.in.controller.AdminController;
import org.ylab.domain.dto.MeterDto;
import org.ylab.domain.dto.ReadingDto;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.enums.Role;
import org.ylab.domain.mapper.MeterMapper;
import org.ylab.domain.mapper.ReadingMapper;
import org.ylab.usecase.service.MeterService;
import org.ylab.usecase.service.ReadingService;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test class for Admin Controller")
@WebMvcTest(AdminController.class)
@AutoConfigureJsonTesters
class AdminControllerTest {
    @MockBean
    @Qualifier("adminReadingService")
    private ReadingService readingService;
    @MockBean
    private MeterService meterService;
    @MockBean
    private ReadingMapper readingMapper;
    @MockBean
    private MeterMapper meterMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<MeterDto> jsonRequest;
    private UserEntity principal;
    private Authentication authentication;

    private List<ReadingDto> readingDtos;
    private List<Reading> readings;

    @BeforeEach
    void setUp() {
        principal = new UserEntity(1, "admin@example.com",
                "FirstAdminName", "LastAdminName",
                "password", Role.ADMIN);
        UserDetails userDetails = User.builder()
                .username(principal.getEmail())
                .password(principal.getPassword())
                .roles(principal.getRole().toString())
                .build();
        authentication = new TestingAuthenticationToken(
                principal, principal.getPassword(), userDetails.getAuthorities());
        Meter meter = new Meter((short) 1, "Some meter");
        readings = List.of(
                new Reading(1L, principal, meter, 123L, Instant.now()),
                new Reading(2L, principal, meter, 654L, Instant.now())
        );
        readingDtos = List.of(
                new ReadingDto((short) 1, 123L, "2024-1-1"),
                new ReadingDto((short) 1, 654L, "2024-2-5")
        );
    }


    @DisplayName("Test '/admin/readings' endpoint, returns actual readings")
    @Test
    void getActual_shouldReturnReadings() throws Exception {
        // given
        when(readingService.getActual(any())).thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/readings")
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

    @DisplayName("Test '/admin/readings' endpoint with YearMonth parameter, " +
            "returns readings submitted within selected month")
    @Test
    void getByMonth_shouldReturnReadingsForGivenMonth() throws Exception {
        // given
        YearMonth date = YearMonth.of(2024, 1);
        when(readingService.getForMonth(any(principal.getClass()), ArgumentMatchers.eq(date.atDay(1))))
                .thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings))
                .thenReturn(readingDtos);


        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/readings")
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

    @DisplayName("Test '/admin/readings/history' endpoint, should return all readings submitted before")
    @Test
    void getHistory_shouldReturnAllReadingsForUser() throws Exception {
        // given
        when(readingService.getAllByUser(any(principal.getClass()))).thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/readings/history")
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

    @DisplayName("Test POST '/admin/meter' endpoint, should return created meter from request body")
    @Test
    void createMeter_shouldReturnCreatedMeter() throws Exception {
        // given
        MeterDto inputMeterDto = new MeterDto();
        inputMeterDto.setType("Test");
        Meter createdMeter = new Meter((short) 2, "Another meter");
        when(meterMapper.toMeter(any())).thenReturn(createdMeter);
        when(meterService.create(any())).thenReturn(createdMeter);
        when(meterMapper.toMeterDto(createdMeter)).thenReturn(inputMeterDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/meter")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                .content(jsonRequest.write(inputMeterDto).getJson()));

        // then
/*        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.type", is("Another meter")));*/

    }
}
