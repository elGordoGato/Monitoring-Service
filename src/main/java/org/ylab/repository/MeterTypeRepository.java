package org.ylab.repository;

import org.ylab.entity.MeterType;

import java.util.List;
import java.util.Optional;

public interface MeterTypeRepository {
    List<MeterType> getAll();

    Optional<MeterType> getById(int id);
}
