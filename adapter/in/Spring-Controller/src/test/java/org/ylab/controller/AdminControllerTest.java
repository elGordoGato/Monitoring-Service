package org.ylab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ylab.dto.ReadingDto;
import org.ylab.dto.UserDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.enums.Role;
import org.ylab.mapper.MeterMapper;
import org.ylab.mapper.ReadingMapper;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {


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

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }



    @Test
    void getActual_shouldReturnReadings() throws Exception {
        // given
        UserEntity principal = new UserEntity(1, "admin@example.com", "FirstAdminName", "LastAdminName", "password", Role.ADMIN);
        Meter meter = new Meter((short) 1, "Some meter");
        List<Reading> readings = List.of(
                new Reading(1L, principal, meter, 123L, Instant.now()),
                new Reading(2L, principal, meter, 654L, Instant.now())
        );
        List<ReadingDto> readingDtos = List.of(
                new ReadingDto((short) 1, 123L, "2024-1-1"),
                new ReadingDto((short) 1, 654L, "2024-2-5")
        );
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

    // write more tests for other methods
}
