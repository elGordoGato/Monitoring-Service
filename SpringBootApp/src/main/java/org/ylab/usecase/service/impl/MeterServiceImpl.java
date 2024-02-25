package org.ylab.usecase.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylab.domain.entity.Meter;
import org.ylab.domain.exception.NotFoundException;
import org.ylab.usecase.port.MeterRepository;
import org.ylab.usecase.service.MeterService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {
    private final MeterRepository typeRepository;

    @Override
    public List<Meter> getAll() {
        return typeRepository.findAll();
    }

    @Override
    public Meter getById(short id) {
        return typeRepository.getById(id).orElseThrow(() ->
                new NotFoundException("This type is not supported"));
    }

    @Override
    @Transactional
    public Meter create(Meter meter) {
        return typeRepository.save(meter);
    }
}
