package org.ylab.repository;

import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ReadingJdbcRepository implements ReadingRepository {

    /**
     * @param reading MeterReading to be saved
     * @return
     */
    @Override
    public Reading save(Reading reading) {
        return null;
    }

    /**
     * @param user User for whom readings to find
     * @param type Type of meter of which readings should be found
     * @return
     */
    @Override
    public Optional<Reading> findLastByUserAndType(User user, Meter type) {
        return Optional.empty();
    }

    /**
     * @param user User for whom actual readings should be found
     * @return
     */
    @Override
    public List<Reading> findActualByUser(User user) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Reading> findActualByAdmin() {
        return null;
    }

    /**
     * @param currentUser User for whom readings to find
     * @param start       first date of month for which need to find readings
     * @param end         last date of month for which need to find readings
     * @return
     */
    @Override
    public List<Reading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end) {
        return null;
    }

    /**
     * @param start first date of month for which need to find readings
     * @param end   last date of month for which need to find readings
     * @return
     */
    @Override
    public List<Reading> findAllByDateBetween(Instant start, Instant end) {
        return null;
    }

    /**
     * @param currentUser User for whom readings to find
     * @return
     */
    @Override
    public List<Reading> findAllByOwner(User currentUser) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Reading> findAll() {
        return null;
    }
}
