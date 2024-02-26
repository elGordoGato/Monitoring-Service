package org.ylab.adapter.in.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ylab.annotations.Loggable;
import org.ylab.domain.dto.ReadingDto;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.adapter.in.mapper.ReadingMapper;
import org.ylab.usecase.service.MeterService;
import org.ylab.usecase.service.ReadingService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Controller to handle requests from user related to reading entity
 */
@Loggable
@Validated
@RestController
@RequestMapping("/readings")
public class ReadingsController {
    private final ReadingService readingService;
    private final MeterService meterService;
    private final ReadingMapper readingMapper;

    public ReadingsController(@Qualifier("userReadingService") ReadingService readingService,
                              MeterService meterService,
                              ReadingMapper readingMapper) {
        this.readingService = readingService;
        this.meterService = meterService;
        this.readingMapper = readingMapper;
    }

    /**
     * @param loggedUser User that is logged in
     * @return List of actual readings  submitted by loggedUser
     */
    @GetMapping
    public List<ReadingDto> getActual(@AuthenticationPrincipal UserEntity loggedUser) {
        List<Reading> foundReadings = readingService.getActual(loggedUser);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser User that is logged in
     * @param date       YearMonth with format "YYYY-MM" for month to get readings
     * @return List of readings submitted within month of selected date by loggedUser
     */
    @GetMapping(params = "date")
    public List<ReadingDto> getByMonth(@AuthenticationPrincipal UserEntity loggedUser,
                                       @RequestParam("date") @PastOrPresent YearMonth date) {
        LocalDate requestDate = date.atDay(1);
        List<Reading> foundReadings = readingService.getForMonth(loggedUser, requestDate);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser User that is logged in
     * @return List of all readings submitted by loggedUser before
     */
    @GetMapping("/history")
    public List<ReadingDto> getHistory(@AuthenticationPrincipal UserEntity loggedUser) {
        List<Reading> foundReadings = readingService.getAllByUser(loggedUser);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param loggedUser   User that is logged in
     * @param inputReading ReadingDto of reading to be saved for this month
     * @return Dto of reading that has been submitted by loggedUser
     */
    @PostMapping()
    public ReadingDto createReadings(@AuthenticationPrincipal UserEntity loggedUser,
                                     @RequestBody @Valid ReadingDto inputReading) {
        Meter meter = meterService.getById(inputReading.getMeterType());
        Long readingValue = inputReading.getReading();

        Reading createdReading = readingService.create(loggedUser, meter, readingValue);
        return readingMapper.toReadingDto(createdReading);
    }
}
