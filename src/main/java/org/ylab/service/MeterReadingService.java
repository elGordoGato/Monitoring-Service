package org.ylab.service;

import org.ylab.entity.MeterReading;
import org.ylab.entity.MeterType;
import org.ylab.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface MeterReadingService {
    List<MeterReading> getActual(User user);

    MeterReading create(User user, MeterType type, long reading);

    List<MeterReading> getForMonth(User currentUser, LocalDate date);

    List<MeterReading> getAllByUser(User currentUser);
}
