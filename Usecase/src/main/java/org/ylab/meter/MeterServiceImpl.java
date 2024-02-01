package org.ylab.meter;

import org.ylab.entity.Meter;
import org.ylab.exception.NotFoundException;
import org.ylab.port.MeterRepository;

import java.util.List;

public class MeterServiceImpl implements MeterService {
    MeterRepository typeRepository;

    public MeterServiceImpl(MeterRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public List<org.ylab.entity.Meter> getAll() {
        return typeRepository.getAll();
    }

    @Override
    public Meter getById(int id) {
        return typeRepository.getById(id).orElseThrow(() ->
                new NotFoundException("This type is not supported"));
    }

    @Override
    public Meter create(Meter meter) {
        return typeRepository.save(meter);
    }
}
