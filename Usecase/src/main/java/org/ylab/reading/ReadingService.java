package org.ylab.reading;

import org.ylab.entity.Meter;
import org.ylab.entity.Reading;
import org.ylab.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface ReadingService {
    /**
     * @param user User that requested to get actual readings
     * @return List of actual readings for user
     */
    List<Reading> getActual(User user);

    /**
     * @param user    User submitting new reading
     * @param type    type of meter for submitting reading
     * @param reading Value of submitting reading
     * @return Reading that has been submitted
     */
    Reading create(User user, Meter type, long reading);

    /**
     * @param currentUser User requesting data
     * @param date        Month when readings shall be found
     * @return List of readings submitted in date
     */
    List<Reading> getForMonth(User currentUser, LocalDate date);

    /**
     * @param currentUser User requesting data
     * @return List of all readings for currentUser
     */
    List<Reading> getAllByUser(User currentUser);
}
