package org.ylab.controller;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/readings")
    public List<ReadingDto> getActual(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getActual(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @GetMapping(value = "/readings", params = {"month", "year"})
    public List<ReadingDto> getByMonth(@AuthenticationPrincipal UserEntity principal,
                                       @RequestParam("month") Integer month,
                                       @RequestParam("year") Integer year) {
        LocalDate requestDate = LocalDate.of(year, month, 1);
        List<Reading> foundReadings = readingService.getForMonth(principal, requestDate);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @GetMapping("/readings/history")
    public List<ReadingDto> getHistory(@AuthenticationPrincipal UserEntity principal) {
        List<Reading> foundReadings = readingService.getAllByUser(principal);
        return readingMapper.toReadingDtoList(foundReadings);
    }

    @PostMapping("/meter")
    public MeterDto createMeter(@RequestBody MeterDto meterDtoToCreate){
        Meter meterToCreate = meterMapper.toMeter(meterDtoToCreate);
        Meter createdMeter = meterService.create(meterToCreate);
        return meterMapper.toMeterDto(createdMeter);
    }
}
