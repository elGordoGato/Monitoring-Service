package org.ylab.service;

import org.ylab.entity.MeterType;

import java.util.List;

public interface MeterTypeService {
    List<MeterType> getAll();

    MeterType getById(int id);
}
