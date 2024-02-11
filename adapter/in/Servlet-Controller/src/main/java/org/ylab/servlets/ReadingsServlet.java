package org.ylab.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ylab.annotations.Loggable;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.enums.Role;
import org.ylab.exception.BadRequestException;
import org.ylab.mapper.ReadingMapper;
import org.ylab.mapper.ReadingMapperImpl;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;
import org.ylab.validation.ReadingDtoValidator;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import static java.time.Instant.now;
import static java.util.Objects.nonNull;

@Loggable
@WebServlet({"/readings/*", "/admin/readings/*"})
public class ReadingsServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final ReadingMapper readingMapper;
    private ReadingService readingAdminService;
    private ReadingService readingUserService;
    private MeterService meterService;

    public ReadingsServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.readingMapper = new ReadingMapperImpl();

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final Object readingUserServiceFromContext = getServletContext().getAttribute("readingServiceByUser");
        final Object readingAdminServiceFromContext = getServletContext().getAttribute("readingServiceByAdmin");
        final Object meterServiceFromContext = getServletContext().getAttribute("meterService");

        if (readingUserServiceFromContext instanceof ReadingService &&
                readingAdminServiceFromContext instanceof ReadingService &&
                meterServiceFromContext instanceof MeterService) {
            this.readingUserService = (ReadingService) readingUserServiceFromContext;
            this.readingAdminService = (ReadingService) readingAdminServiceFromContext;
            this.meterService = (MeterService) meterServiceFromContext;
        } else {
            throw new IllegalStateException("Repo has not been initialized!");
        }
    }

    /**
     * @param req  handles request to get actual, by month or all readings
     * @param resp Http status 200, json with list of found readings
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        ReadingService readingServiceWithRights;
        if (path.startsWith("/admin")) {
            readingServiceWithRights = readingAdminService;
        } else readingServiceWithRights = readingUserService;

        String monthParam = req.getParameter("month");
        String yearParam = req.getParameter("year");
        User user = (User) req.getSession().getAttribute("user");

        List<ReadingDto> foundReadings;

        if (path.startsWith("/readings/history") ||
                path.startsWith("/admin/readings/history")) {
            foundReadings = handleHistoryRequest(user, readingServiceWithRights);
        } else if (nonNull(monthParam) && nonNull(yearParam)) {
            foundReadings = handleRequestByDate(monthParam, yearParam, user, readingServiceWithRights);
        } else foundReadings = handleActualRequest(user, readingServiceWithRights);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(
                objectMapper.writeValueAsString(foundReadings));
    }

    /**
     * @param req  contain reading json to be submitted by user
     * @param resp Http status 201 and created reading json
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");

        if (!user.getRole().equals(Role.USER)) {
            throw new AccessDeniedException("Only user can submit readings");
        }

        ReadingDto inputReading = new ObjectMapper().readValue(
                req.getReader(), ReadingDto.class);

        log(String.format("%s - Received request to submit readings by user with id: %s\n%s",
                now(), user.getId(), inputReading));

        ReadingDtoValidator.validateReadingDto(inputReading);

        Meter meter = meterService.getById(inputReading.getMeterType());
        Long readingValue = inputReading.getReading();

        Reading createdReading = readingUserService.create(user, meter, readingValue);
        ReadingDto createdReadingDto = readingMapper.dtoFromEntity(createdReading);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getWriter().write(
                objectMapper.writeValueAsString(createdReadingDto));
    }

    private List<ReadingDto> handleActualRequest(User currentUser, ReadingService readingService) {
        log(now() + " - Received request to get actual readings by user with id: " + currentUser.getId());
        List<Reading> readings = readingService.getActual(currentUser);
        return readingMapper.dtoListFromEntity(readings);
    }

    private List<ReadingDto> handleRequestByDate(
            String monthParam, String yearParam, User currentUser, ReadingService readingService) {
        log(String.format("%s - Received request from user with id: %s to get readings for %s.%s\n",
                now(), currentUser.getId(), monthParam, yearParam));
        int month;
        int year;
        LocalDate date;

        try {
            month = Integer.parseInt(monthParam);
            year = Integer.parseInt(yearParam);
            date = LocalDate.of(year, month, 1);
        } catch (NumberFormatException | DateTimeException e) {
            throw new BadRequestException(e.getMessage());
        }
        List<Reading> foundReadings = readingService.getForMonth(currentUser, date);
        return readingMapper.dtoListFromEntity(foundReadings);
    }

    private List<ReadingDto> handleHistoryRequest(User user, ReadingService readingService) {
        log(now() + " - Received request to get all history of readings for user with id: " + user.getId());
        List<Reading> readings = readingService.getAllByUser(user);
        return readingMapper.dtoListFromEntity(readings);
    }
}
