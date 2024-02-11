package org.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.enums.Role;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;

import java.io.*;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingsServletTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter printWriter = new PrintWriter(stringWriter);
    @Mock
    private ReadingService readingUserService;
    @Mock
    private MeterService meterService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private User user;
    @InjectMocks
    private ReadingsServlet readingsServlet;

    @Test
    public void testDoPost() throws IOException {
        ReadingDto inputReading = new ReadingDto((short) 1, 12345L, null);
        byte[] readingJson = objectMapper.writeValueAsBytes(inputReading);
        InputStream inputStream = new ByteArrayInputStream(readingJson);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Meter meter = new Meter((short) 1, "Some meter type");
        Instant collectedDate = Instant.now();
        Reading createdReading = new Reading(42L, user, meter, inputReading.getReading(), collectedDate);

        when(user.getRole()).thenReturn(Role.USER);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getReader()).thenReturn(bufferedReader);
        when(meterService.getById(inputReading.getMeterType())).thenReturn(meter);
        when(readingUserService.create(user, meter, inputReading.getReading())).thenReturn(createdReading);
        when(response.getWriter()).thenReturn(printWriter);

        readingsServlet.doPost(request, response);

        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(meterService).getById(inputReading.getMeterType());
        verify(readingUserService).create(user, meter, inputReading.getReading());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType("application/json");
    }

    @Test
    public void testDoGet() throws IOException {
        Reading reading = new Reading(
                42L, user, new Meter((short) 2, "meter type"), 12345L, Instant.now());
        List<Reading> foundReadings = List.of(reading);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getServletPath()).thenReturn("/readings/actual");
        when(readingUserService.getActual(user)).thenReturn(foundReadings);
        when(response.getWriter()).thenReturn(printWriter);

        readingsServlet.doGet(request, response);

        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(request).getServletPath();
        verify(readingUserService).getActual(user);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
    }
}