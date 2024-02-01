package org.ylab.port;


import org.ylab.entity.Meter;

import java.util.List;
import java.util.Optional;

public interface MeterRepository {

    /**
     * @return List of all available types of meter
     */
    List<Meter> getAll();

    /**
     * @param id ID of meter type
     * @return Meter with id
     */
    Optional<Meter> getById(int id);

    /**
     * @param meter new type of meter to be saved to db
     * @return Mater that have been saved
     */
    Meter save(Meter meter);
}
