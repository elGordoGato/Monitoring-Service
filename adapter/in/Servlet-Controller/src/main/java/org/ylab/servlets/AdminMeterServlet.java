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
import org.ylab.dto.MeterDto;
import org.ylab.entity.Meter;
import org.ylab.mapper.MeterMapper;
import org.ylab.mapper.MeterMapperImpl;
import org.ylab.meter.MeterService;
import org.ylab.validation.MeterDtoValidator;

import java.io.IOException;
import java.time.Instant;

@Loggable
@WebServlet(urlPatterns = "/admin/meter")
public class AdminMeterServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final MeterMapper meterMapper;
    private MeterService meterService;

    public AdminMeterServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.meterMapper = new MeterMapperImpl();

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final Object meterServiceFromContext = getServletContext().getAttribute("meterService");

        if (meterServiceFromContext instanceof MeterService) {
            this.meterService = (MeterService) meterServiceFromContext;
        } else {
            throw new IllegalStateException("Repo has not been initialized!");
        }
    }

    /**
     * Handling creating new meter type by admin,
     * expects proper MeterDto json in request body
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        MeterDto meterToCreate = objectMapper.readValue(
                req.getReader(), MeterDto.class);
        getServletContext().log(
                Instant.now() + " - Received request from admin to create new meter type: " +
                        meterToCreate.getType());

        MeterDtoValidator.validateMeterDto(meterToCreate);

        Meter createdMeter = meterService.create(
                meterMapper.dtoToEntity(meterToCreate));
        MeterDto createdMeterDto = meterMapper.entityToDto(createdMeter);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getWriter().write(
                objectMapper.writeValueAsString(createdMeterDto));
    }
}
