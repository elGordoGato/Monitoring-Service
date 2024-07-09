package org.ylab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ylab.dto.MeterDto;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;
import org.ylab.mapper.MeterMapper;
import org.ylab.mapper.ReadingMapper;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;

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
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {


    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ReadingService readingService;
    @Mock
    private MeterService meterService;
    @Mock
    private ReadingMapper readingMapper;
    @Mock
    private MeterMapper meterMapper;
    @InjectMocks
    private AdminController controller;
    private MockMvc mockMvc;
    private UserEntity principal;

    private List<ReadingDto> readingDtos;
    private List<Reading> readings;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        principal = new UserEntity(1, "admin@example.com",
                "FirstAdminName", "LastAdminName",
                "password", Role.ADMIN);
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
                .principal(new TestingAuthenticationToken(principal, null)));

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
        when(readingService.getForMonth(any(principal.getClass()), ArgumentMatchers.eq(date.atDay(1)))).thenReturn(readings);
        when(readingMapper.toReadingDtoList(readings)).thenReturn(readingDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/admin/readings")
                .principal(new TestingAuthenticationToken(principal, null))
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
                .principal(new TestingAuthenticationToken(principal, null)));

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
        MeterDto inputMeterDto = new MeterDto((short) 2, "Another meter");
        Meter createdMeter = new Meter((short) 2, "Another meter");
        when(meterMapper.toMeter(inputMeterDto)).thenReturn(createdMeter);
        when(meterService.create(createdMeter)).thenReturn(createdMeter);
        when(meterMapper.toMeterDto(createdMeter)).thenReturn(inputMeterDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/admin/meter")
                .principal(new TestingAuthenticationToken(principal, null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputMeterDto)));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.type", is("Another meter")));
    }
}
