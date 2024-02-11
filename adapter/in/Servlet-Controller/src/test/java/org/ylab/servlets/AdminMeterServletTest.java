package org.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.dto.MeterDto;
import org.ylab.entity.Meter;
import org.ylab.meter.MeterService;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMeterServletTest {
    @Mock
    private MeterService meterService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private AdminMeterServlet servlet;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }


    @Test
    @DisplayName("When call doPost then return dto with same type")
    void doPost() throws IOException {
        MeterDto meterDto = new MeterDto(null, "example meter");
        Meter meter = new Meter((short) 1, "example meter");

        String meterJson = objectMapper.writeValueAsString(meterDto);
        InputStream inputStream = new ByteArrayInputStream(meterJson.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        when(request.getReader()).thenReturn(bufferedReader);
        when(meterService.create(any(Meter.class))).thenReturn(meter);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType("application/json");
        printWriter.flush();
        String responseOutput = stringWriter.toString();
        MeterDto responseMeterDto = objectMapper.readValue(responseOutput, MeterDto.class);
        assertThat(responseMeterDto).extracting("type").isEqualTo(meterDto.getType());
    }
}