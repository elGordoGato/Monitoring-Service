package org.ylab.port;

import org.ylab.entity.Reading;
import org.ylab.entity.Meter;
import org.ylab.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReadingRepository {
    List<Reading> findActualByUser(User user);

    Optional<Reading> getLastByUserAndType(User user, Meter type);

    Reading save(Reading reading);

    List<Reading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end);

    List<Reading> findAllByOwner(User currentUser);

    List<Reading> findActualByAdmin();

    List<Reading> findAllByDateBetween(Instant start, Instant end);

    List<Reading> findAll();
}
