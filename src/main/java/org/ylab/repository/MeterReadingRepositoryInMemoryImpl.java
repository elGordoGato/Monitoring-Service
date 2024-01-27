package org.ylab.repository;

import org.ylab.entity.MeterReading;
import org.ylab.entity.MeterType;
import org.ylab.entity.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MeterReadingRepositoryInMemoryImpl implements MeterReadingRepository {
    Map<User, List<MeterReading>> readingMap = new HashMap<>();


    /**
     * @param user User for whom readings to find
     * @return A list of actual readings for user
     */
    @Override
    public List<MeterReading> findActual(User user) {
        List<MeterReading> meterReadingList = readingMap.getOrDefault(user, new ArrayList<>());
        Map<MeterType, Optional<MeterReading>> readingsByType = meterReadingList.stream()
                .collect(Collectors.groupingBy(
                        MeterReading::getMeter, Collectors.maxBy(
                                Comparator.comparing(MeterReading::getCollectedDate))));
        List<MeterReading> resultList = new ArrayList<>();
        readingsByType.values().forEach(o -> o.ifPresent(resultList::add));
        return resultList;
    }

    /**
     * @param user User for whom readings to find
     * @param type Type of meter of which readings should be found
     * @return Optional of last MeterReading submitted by user of type
     */
    @Override
    public Optional<MeterReading> getLastByUserAndType(User user, MeterType type) {
       List<MeterReading> readingsByUser = readingMap.get(user);
        if (readingsByUser != null) {
            return readingsByUser.stream()
                    .filter(r -> r.getMeter().equals(type))
                    .max(Comparator.comparing(MeterReading::getCollectedDate));
        }
        return Optional.empty();
    }

    /**
     * @param meterReading MeterReading to be saved
     * @return MeterReading that was saved
     */
    @Override
    public MeterReading save(MeterReading meterReading) {
        meterReading.setCollectedDate(Instant.now());
        List<MeterReading> readings = readingMap.getOrDefault(meterReading.getOwner(), new ArrayList<>());

        readings.add(meterReading);

        readingMap.put(meterReading.getOwner(), readings);

        return meterReading;
    }

    /**
     * @param currentUser User for whom readings to find
     * @param start first date of month for which need to find readings
     * @param end last date of month for which need to find readings
     * @return A list of all readings submitted by user within selected month
     */
    @Override
    public List<MeterReading> findAllByOwnerAndDateBetween(User currentUser, Instant start, Instant end) {
        List<MeterReading> meterReadings = readingMap.get(currentUser);
        if (meterReadings != null) {
            return meterReadings.stream()
                    .filter(r ->
                            r.getCollectedDate().isAfter(start) &&
                            r.getCollectedDate().isBefore(end))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * @param currentUser User for whom readings to find
     * @return A list of all readings submitted by currentUser
     */
    @Override
    public List<MeterReading> findAllByOwner(User currentUser) {
       return readingMap.getOrDefault(currentUser, new ArrayList<>());
    }
}
