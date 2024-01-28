package org.ylab;



import org.ylab.entity.Meter;
import org.ylab.port.MeterRepository;

import java.util.*;

public class MeterRepositoryInMemory implements MeterRepository {
    Map<Integer, Meter> meterTypes = new HashMap<>(2);

    public MeterRepositoryInMemory() {
        Meter coldWater = new Meter();
        coldWater.setId(1);
        coldWater.setName("Cold water");
        meterTypes.put(1, coldWater);
        Meter hotWater = new Meter();
        hotWater.setId(2);
        hotWater.setName("Hot water");
        meterTypes.put(2, hotWater);
    }

    /**
     * @return
     */
    @Override
    public List<Meter> getAll() {
        return new ArrayList<>(meterTypes.values());
    }

    @Override
    public Optional<Meter> getById(int id){
        return Optional.ofNullable(meterTypes.get(id));
    }
}
