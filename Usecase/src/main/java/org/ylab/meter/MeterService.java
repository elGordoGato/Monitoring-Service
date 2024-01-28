package org.ylab.meter;


import org.ylab.entity.Meter;

import java.util.List;

public interface MeterService {
    List<Meter> getAll();

    Meter getById(int id);
}
