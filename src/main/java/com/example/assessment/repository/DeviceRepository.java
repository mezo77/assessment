package com.example.assessment.repository;

import com.example.assessment.entity.DeviceEntity;
import com.example.assessment.model.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
    List<DeviceEntity> findByBrand(String brand);
    List<DeviceEntity> findByState(DeviceState state);
}
