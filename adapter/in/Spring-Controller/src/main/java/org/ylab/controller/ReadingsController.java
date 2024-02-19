package org.ylab.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ylab.dto.MeterDto;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.mapper.MeterMapper;
import org.ylab.mapper.ReadingMapper;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;

import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Validated
@RestController
@RequestMapping("/readings")
public class ReadingsController {
    private final ReadingService readingService;
    private final MeterService meterService;
    private final ReadingMapper readingMapper;
    private final MeterMapper meterMapper;

    public ReadingsController(@Qualifier("userReadingService") ReadingService readingService,
                           MeterService meterService,
                           ReadingMapper readingMapper, MeterMapper meterMapper) {
        this.readingService = readingService;
        this.meterService = meterService;
        this.readingMapper = readingMapper;
        this.meterMapper = meterMapper;
    }

    @GetMapping
    public List<ReadingDto> getActual(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getActual(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @GetMapping(params = "date")
    public List<ReadingDto> getByMonth(@AuthenticationPrincipal UserEntity principal,
                                       @RequestParam("date") @PastOrPresent YearMonth date) {
        LocalDate requestDate = date.atDay(1);
        List<Reading> foundReadings = readingService.getForMonth(principal, requestDate);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @GetMapping("/history")
    public List<ReadingDto> getHistory(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getAllByUser(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @PostMapping()
    public ReadingDto createReadings(@AuthenticationPrincipal UserEntity principal,
                                   @RequestBody @Valid ReadingDto inputReading){
        Meter meter = meterService.getById(inputReading.getMeterType());
        Long readingValue = inputReading.getReading();

        Reading createdReading = readingService.create(principal, meter, readingValue);
        return readingMapper.toReadingDto(createdReading);
    }
}
