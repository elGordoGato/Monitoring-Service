package org.ylab.usecase.readingService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.ylab.domain.entity.Reading;
import org.ylab.domain.entity.UserEntity;
import org.ylab.usecase.port.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Qualifier("adminReadingService")
public class ReadingAdminServiceImpl extends ReadingServiceImpl {
    public ReadingAdminServiceImpl(ReadingRepository readingRepository) {
        super(readingRepository);
    }

    @Override
    public List<Reading> getActual(UserEntity user) {
        return readingRepository.findActualByAdmin();
    }

    @Override
    public List<Reading> getForMonth(UserEntity currentUser, LocalDate date) {
        Instant start = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return readingRepository.findAllByDateBetween(start, end);
    }

    @Override
    public List<Reading> getAllByUser(UserEntity currentUser) {
        return readingRepository.findAll();
    }
}
