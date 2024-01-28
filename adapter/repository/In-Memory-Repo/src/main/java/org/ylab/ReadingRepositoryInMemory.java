package org.ylab;

import org.ylab.entity.Reading;
import org.ylab.entity.Meter;
import org.ylab.entity.User;
import org.ylab.port.ReadingRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ReadingRepositoryInMemory implements ReadingRepository {
    Map<User, List<Reading>> readingMap = new HashMap<>();


    /**
     * @param user User for whom readings to find
     * @return A list of actual readings for user
     */
    @Override
    public List<Reading> findActualByUser(User user) {
        List<Reading> readingList = readingMap.getOrDefault(user, new ArrayList<>());
        return findActualForList(readingList);
    }

    /**
     * @return
     */
    @Override
    public List<Reading> findActualByAdmin() {
        List<Reading> readingList = findAll();
        return findActualForList(readingList);
    }


    /**
     * @param user User for whom readings to find
     * @param type Type of meter of which readings should be found
     * @return Optional of last MeterReading submitted by user of type
     */
    @Override
    public Optional<Reading> getLastByUserAndType(User user, Meter type) {
       List<Reading> readingsByUser = readingMap.get(user);
        if (readingsByUser != null) {
            return readingsByUser.stream()
                    .filter(r -> r.getMeter().equals(type))
                    .max(Comparator.comparing(Reading::getCollectedDate));
        }
        return Optional.empty();
    }

    /**
     * @param reading MeterReading to be saved
     * @return MeterReading that was saved
     */
    @Override
    public Reading save(Reading reading) {
        reading.setCollectedDate(Instant.now());
        List<Reading> readings = readingMap.getOrDefault(reading.getOwner(), new ArrayList<>());

        readings.add(reading);

        readingMap.put(reading.getOwner(), readings);

        return reading;
    }

    /**
     * @param currentUser User for whom readings to find
     * @param start first date of month for which need to find readings
     * @param end last date of month for which need to find readings
     * @return A list of all readings submitted by user within selected month
     */
    @Override
    public List<Reading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end) {
        List<Reading> readings = readingMap.get(currentUser);
        if (readings != null) {
            return readings.stream()
                    .filter(r ->
                            r.getCollectedDate().isAfter(start) &&
                            r.getCollectedDate().isBefore(end))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<Reading> findAllByDateBetween(Instant start, Instant end) {
        return findAll().stream()
                .filter(r ->
                        r.getCollectedDate().isAfter(start) &&
                                r.getCollectedDate().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * @param currentUser User for whom readings to find
     * @return A list of all readings submitted by currentUser
     */
    @Override
    public List<Reading> findAllByOwner(User currentUser) {
       return readingMap.getOrDefault(currentUser, new ArrayList<>());
    }

    /**
     * @return
     */
    @Override
    public List<Reading> findAll() {
        List<Reading> readingList = new ArrayList<>();
        readingMap.values().forEach(readingList::addAll);
        return readingList;
    }

    private List<Reading> findActualForList(List<Reading> readingList) {
        Map<Meter, Optional<Reading>> readingsByType = readingList.stream()
                .collect(Collectors.groupingBy(
                        Reading::getMeter, Collectors.maxBy(
                                Comparator.comparing(Reading::getCollectedDate))));
        List<Reading> resultList = new ArrayList<>();
        readingsByType.values().forEach(o -> o.ifPresent(resultList::add));
        return resultList;
    }
}
