package org.ylab.service;

import org.ylab.entity.MeterType;
import org.ylab.exception.NotFoundException;
import org.ylab.repository.MeterTypeRepository;
import org.ylab.repository.MeterTypeRepositoryInMemoryImpl;

import java.util.List;

public class MeterTypeServiceImpl implements MeterTypeService {
    MeterTypeRepository typeRepository = new MeterTypeRepositoryInMemoryImpl();
    /**
     * @return
     */
    @Override
    public List<MeterType> getAll() {
        return typeRepository.getAll();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public MeterType getById(int id) {
       return typeRepository.getById(id).orElseThrow(() ->
               new NotFoundException("This type is not supported"));
    }
}
