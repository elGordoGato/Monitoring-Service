package org.ylab.usecase.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.exception.ConflictException;
import org.ylab.usecase.port.ReadingRepository;
import org.ylab.usecase.service.ReadingService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Qualifier("userReadingService")
@Transactional(readOnly = true)
public class ReadingServiceImpl implements ReadingService {
    final ReadingRepository readingRepository;

    public ReadingServiceImpl(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @Override
    public List<Reading> getActual(UserEntity user) {
        return readingRepository.findActualByUser(user);
    }

    @Override
    @Transactional
    public Reading create(UserEntity requestingUser, Meter meterType, long readingValue) {
        readingRepository.findLastByUserAndType(requestingUser, meterType)
                .ifPresent(r -> {
                    LocalDate collectedDate = LocalDate.ofInstant(r.getCollectedDate(), ZoneId.systemDefault());
                    if (collectedDate.getYear() == LocalDate.now().getYear() &&
                            collectedDate.getMonth().equals(LocalDate.now().getMonth())
                    ) {
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
    public List<Reading> getForMonth(UserEntity currentUser, LocalDate date) {
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return readingRepository.findAllByOwnerAndDateBetween(currentUser, start, end);
    }

    @Override
    public List<Reading> getAllByUser(UserEntity currentUser) {
        return readingRepository.findAllByOwner(currentUser);
    }
}
