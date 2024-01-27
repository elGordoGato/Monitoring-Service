package org.ylab.repository;

import org.ylab.entity.MeterReading;
import org.ylab.entity.MeterType;
import org.ylab.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository {
    List<MeterReading> findActual(User user);

    Optional<MeterReading> getLastByUserAndType(User user, MeterType type);

    MeterReading save(MeterReading meterReading);

    List<MeterReading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end);

    List<MeterReading> findAllByOwner(User currentUser);
}
