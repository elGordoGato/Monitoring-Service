package org.ylab.reading;

import org.ylab.entity.Reading;
import org.ylab.entity.Meter;
import org.ylab.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface ReadingService {
    List<Reading> getActual(User user);

    Reading create(User user, Meter type, long reading);

    List<Reading> getForMonth(User currentUser, LocalDate date);

    List<Reading> getAllByUser(User currentUser);
}
