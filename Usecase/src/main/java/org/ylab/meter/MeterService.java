package org.ylab.meter;


import org.ylab.entity.Meter;

import java.util.List;

public interface MeterService {
    /**
     * @return List of all available meter types
     */
    List<Meter> getAll();

    /**
     * @param id ID of meter type to get
     * @return Meter type with id
     */
    Meter getById(int id);

    /**
     * @param meter new type of meter to be created
     * @return Mater that have been saved
     */
    Meter create(Meter meter);
}
