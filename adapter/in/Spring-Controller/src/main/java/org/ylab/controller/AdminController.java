package org.ylab.controller;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylab.dto.ReadingDto;
import org.ylab.entity.Reading;
import org.ylab.entity.UserEntity;
import org.ylab.mapper.ReadingMapper;
import org.ylab.reading.ReadingService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final ReadingService readingService;
    private final ReadingMapper readingMapper;

    public AdminController(@Qualifier("adminReadingService") ReadingService readingService, ReadingMapper readingMapper) {
        this.readingService = readingService;
        this.readingMapper = readingMapper;
    }

    @GetMapping("/readings")
    public List<ReadingDto> getActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticationPrincipal = (User) authentication.getPrincipal();
        UserEntity currentUser = new UserEntity();
        currentUser.setId(Integer.valueOf(authenticationPrincipal.getUsername()));
        List<Reading> foundReadings = readingService.getActual(currentUser);
        return readingMapper.toReadingDtoList(foundReadings);
    }
}
