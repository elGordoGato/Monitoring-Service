package org.ylab.meter;

import org.springframework.stereotype.Service;
import org.ylab.entity.Meter;
import org.ylab.exception.NotFoundException;
import org.ylab.port.MeterRepository;

import java.util.List;

@Service
public class MeterServiceImpl implements MeterService {
    private final MeterRepository typeRepository;

    public MeterServiceImpl(MeterRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public List<org.ylab.entity.Meter> getAll() {
        return typeRepository.findAll();
    }

    @Override
    public Meter getById(short id) {
        return typeRepository.getById(id).orElseThrow(() ->
                new NotFoundException("This type is not supported"));
    }

    @Override
    public Meter create(Meter meter) {
        return typeRepository.save(meter);
    }
}
