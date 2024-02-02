package org.ylab;


import org.ylab.entity.Meter;
import org.ylab.port.MeterRepository;

import java.util.*;

public class MeterRepositoryInMemory implements MeterRepository {
    private final Map<Integer, Meter> meterTypes = new HashMap<>();
    private int idCounter = 1;

    public MeterRepositoryInMemory() {
        Meter coldWater = new Meter();
        coldWater.setId(idCounter++);
        coldWater.setType("Cold water");
        meterTypes.put(1, coldWater);
        Meter hotWater = new Meter();
        hotWater.setId(idCounter++);
        hotWater.setType("Hot water");
        meterTypes.put(2, hotWater);
    }

    @Override
    public List<Meter> findAll() {
        return new ArrayList<>(meterTypes.values());
    }

    @Override
    public Optional<Meter> getById(int id) {
        return Optional.ofNullable(meterTypes.get(id));
    }

    @Override
    public Meter save(Meter meter) {
        meter.setId(idCounter++);
        meterTypes.put(meter.getId(), meter);
        return meter;
    }
}
