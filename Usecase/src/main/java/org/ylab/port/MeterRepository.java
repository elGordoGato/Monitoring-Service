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
}
