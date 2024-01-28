package org.ylab.port;


import org.ylab.entity.Meter;

import java.util.List;
import java.util.Optional;

public interface MeterRepository {
    List<Meter> getAll();

    Optional<Meter> getById(int id);
}
