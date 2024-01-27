package org.ylab.repository;

import org.ylab.entity.MeterType;

import java.util.*;

public class MeterTypeRepositoryInMemoryImpl implements MeterTypeRepository {
    Map<Integer, MeterType> meterTypes = new HashMap<>(2);

    public MeterTypeRepositoryInMemoryImpl() {
        MeterType coldWater = new MeterType();
        coldWater.setId(1);
        coldWater.setName("Cold water");
        meterTypes.put(1, coldWater);
        MeterType hotWater = new MeterType();
        hotWater.setId(2);
        hotWater.setName("Hot water");
        meterTypes.put(2, hotWater);
    }

    /**
     * @return
     */
    @Override
    public List<MeterType> getAll() {
        return new ArrayList<>(meterTypes.values());
    }

    @Override
    public Optional<MeterType> getById(int id){
        return Optional.ofNullable(meterTypes.get(id));
    }
}
