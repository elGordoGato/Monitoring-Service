package org.ylab.adapter.in.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ylab.annotations.Auditable;
import org.ylab.annotations.Loggable;
import org.ylab.domain.dto.MeterDto;
import org.ylab.domain.dto.ReadingDto;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.mapper.MeterMapper;
import org.ylab.domain.mapper.ReadingMapper;
import org.ylab.usecase.service.MeterService;
import org.ylab.usecase.service.ReadingService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Controller class to handle requests from admin
 */
@Auditable
@Loggable
@Validated
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final ReadingService readingService;
    private final MeterService meterService;
    private final ReadingMapper readingMapper;
    private final MeterMapper meterMapper;

    public AdminController(@Qualifier("adminReadingService") ReadingService readingService,
                           MeterService meterService,
                           ReadingMapper readingMapper, MeterMapper meterMapper) {
        this.readingService = readingService;
        this.meterService = meterService;
        this.readingMapper = readingMapper;
        this.meterMapper = meterMapper;
    }

    /**
     * @param loggedUser Logged admin
     * @return List of actual readings
     */
    @GetMapping("/readings")
    public List<ReadingDto> getActual(@AuthenticationPrincipal UserEntity loggedUser) {
        List<Reading> foundReadings = readingService.getActual(loggedUser);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser Logged admin
     * @param date       YearMonth with format "YYYY-MM" for month to get readings
     * @return List of readings submitted within month of selected date
     */
    @GetMapping(value = "/readings", params = "date")
    public List<ReadingDto> getByMonth(@AuthenticationPrincipal UserEntity loggedUser,
                                       @RequestParam("date") @PastOrPresent YearMonth date) {
        LocalDate requestDate = date.atDay(1);
        List<Reading> foundReadings = readingService.getForMonth(loggedUser, requestDate);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser Logged admin
     * @return List of all readings submitted before
     */
    @GetMapping("/readings/history")
    public List<ReadingDto> getHistory(@AuthenticationPrincipal UserEntity loggedUser) {
        List<Reading> foundReadings = readingService.getAllByUser(loggedUser);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser Logged admin
     * @param inputMeterDto MeterDto json to be created
     * @return Dto of created Meter
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/meter")
    public MeterDto createMeter(@AuthenticationPrincipal UserEntity loggedUser,
                                @RequestBody @Valid MeterDto inputMeterDto) {
        Meter meterToCreate = meterMapper.toMeter(inputMeterDto);
        Meter createdMeter = meterService.create(meterToCreate);
        return meterMapper.toMeterDto(createdMeter);
    }
}
