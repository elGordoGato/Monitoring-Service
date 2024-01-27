package org.ylab.service;

import org.ylab.entity.MeterReading;
import org.ylab.entity.MeterType;
import org.ylab.entity.User;
import org.ylab.exception.ConflictException;
import org.ylab.repository.MeterReadingRepository;
import org.ylab.repository.MeterReadingRepositoryInMemoryImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class MeterReadingServiceImpl implements MeterReadingService {
    MeterReadingRepository meterReadingRepository = new MeterReadingRepositoryInMemoryImpl();

    /**
     * @param user
     * @return
     */
    @Override
    public List<MeterReading> getActual(User user) {
        return meterReadingRepository.findActual(user);
    }

    @Override
    public MeterReading create(User user, MeterType type, long reading) {
        meterReadingRepository.getLastByUserAndType(user, type)
                .ifPresent(r -> {
                    if (LocalDate.ofInstant(r.getCollectedDate(), ZoneId.systemDefault()).getMonth()
                            .equals(LocalDate.now().getMonth())) {
                        throw new ConflictException("Readings fot this meter were already collected in this month");
                    }
                });
        MeterReading meterReading = new MeterReading();
        meterReading.setMeter(type);
        meterReading.setOwner(user);
        meterReading.setReading(reading);
        return meterReadingRepository.save(meterReading);
    }

    /**
     * @param currentUser
     * @param date
     * @return
     */
    @Override
    public List<MeterReading> getForMonth(User currentUser, LocalDate date) {
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return meterReadingRepository.findAllByOwnerAndDateBetween(currentUser, start, end);
    }

    /**
     * @param currentUser
     * @return
     */
    @Override
    public List<MeterReading> getAllByUser(User currentUser) {
        return meterReadingRepository.findAllByOwner(currentUser);
    }
}
