package org.ylab.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ylab.annotations.Loggable;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.mapper.ReadingMapper;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;

import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
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
     * @param principal User that is logged in
     * @return List of actual readings  submitted by principal
     */
    @GetMapping
    public List<ReadingDto> getActual(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getActual(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param principal User that is logged in
     * @param date      YearMonth with format "YYYY-MM" for month to get readings
     * @return List of readings submitted within month of selected date by principal
     */
    @GetMapping(params = "date")
    public List<ReadingDto> getByMonth(@AuthenticationPrincipal UserEntity principal,
                                       @RequestParam("date") @PastOrPresent YearMonth date) {
        LocalDate requestDate = date.atDay(1);
        List<Reading> foundReadings = readingService.getForMonth(principal, requestDate);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param principal User that is logged in
     * @return List of all readings submitted by principal before
     */
    @GetMapping("/history")
    public List<ReadingDto> getHistory(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getAllByUser(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    /**
     * @param principal    User that is logged in
     * @param inputReading ReadingDto of reading to be saved for this month
     * @return Dto of reading that has been submitted by principal
     */
    @PostMapping()
    public ReadingDto createReadings(@AuthenticationPrincipal UserEntity principal,
                                     @RequestBody @Valid ReadingDto inputReading) {
        Meter meter = meterService.getById(inputReading.getMeterType());
        Long readingValue = inputReading.getReading();

        Reading createdReading = readingService.create(principal, meter, readingValue);
        return readingMapper.toReadingDto(createdReading);
    }
}
