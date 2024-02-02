package org.ylab.reading;

import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.exception.ConflictException;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ReadingServiceImpl implements ReadingService {
    final ReadingRepository readingRepository;

    public ReadingServiceImpl(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @Override
    public List<Reading> getActual(User user) {
        return readingRepository.findActualByUser(user);
    }

    @Override
    public Reading create(User requestingUser, Meter meterType, long readingValue) {
        readingRepository.findLastByUserAndType(requestingUser, meterType)
                .ifPresent(r -> {
                    if (LocalDate.ofInstant(r.getCollectedDate(), ZoneId.systemDefault()).getMonth()
                            .equals(LocalDate.now().getMonth())) {
                        throw new ConflictException("Readings fot this meter were already collected in this month");
                    }
                });
        Reading meterReading = new Reading();
        meterReading.setMeter(meterType);
        meterReading.setOwner(requestingUser);
        meterReading.setReading(readingValue);
        return readingRepository.save(meterReading);
    }

    @Override
    public List<Reading> getForMonth(User currentUser, LocalDate date) {
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return readingRepository.findAllByOwnerAndDateBetween(currentUser, start, end);
    }

    @Override
    public List<Reading> getAllByUser(User currentUser) {
        return readingRepository.findAllByOwner(currentUser);
    }
}
