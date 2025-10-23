package com.example.assessment.service;

import com.example.assessment.exception.DeviceInUseException;
import com.example.assessment.exception.DeviceNotFoundException;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.entity.DeviceEntity;
import com.example.assessment.model.DeviceState;
import com.example.assessment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;

    public DeviceDTO createDevice(DeviceDTO deviceDTO) {
        logger.info("Creating device with name: {}", deviceDTO.getName());
        DeviceEntity entity = mapToEntity(deviceDTO);
        entity.setCreationTime(LocalDateTime.now());
        DeviceEntity saved = deviceRepository.save(entity);
        logger.info("Device created successfully with id: {}", saved.getId());
        return mapToDto(saved);
    }

    public List<DeviceDTO> getAllDevices() {
        logger.debug("Fetching all devices");
        return deviceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Page<DeviceDTO> getAllDevices(Pageable pageable) {
        logger.debug("Fetching paged devices: {}", pageable);
        Page<DeviceEntity> page = deviceRepository.findAll(pageable);
        List<DeviceDTO> dtos = page.stream().map(this::mapToDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public DeviceDTO getDeviceById(Long id) {
        logger.debug("Fetching device with id: {}", id);
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Device not found with id: {}", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });
        return mapToDto(entity);
    }

    public DeviceDTO updateDevice(Long id, DeviceDTO deviceDTO) {
        logger.info("Updating device with id: {}", id);
        DeviceEntity existing = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Device not found with id: {} for update", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });
        if (deviceDTO.getName() != null && existing.getState() == DeviceState.IN_USE) {
            logger.warn("Attempted to update name of in-use device with id: {}", id);
            throw new DeviceInUseException("Cannot update name when device is in use");
        }
        if (deviceDTO.getBrand() != null && existing.getState() == DeviceState.IN_USE) {
            logger.warn("Attempted to update brand of in-use device with id: {}", id);
            throw new DeviceInUseException("Cannot update brand when device is in use");
        }
        existing.setName(deviceDTO.getName());
        existing.setBrand(deviceDTO.getBrand());
        existing.setState(deviceDTO.getState());
        // creationTime not updated
        DeviceEntity saved = deviceRepository.save(existing);
        logger.info("Device updated successfully with id: {}", id);
        return mapToDto(saved);
    }

    public DeviceDTO partialUpdateDevice(Long id, DeviceDTO deviceDTO) {
        logger.info("Partially updating device with id: {}", id);
        DeviceEntity existing = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Device not found with id: {} for partial update", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });
        if (deviceDTO.getName() != null) {
            if (existing.getState() == DeviceState.IN_USE) {
                logger.warn("Attempted to update name of in-use device with id: {}", id);
                throw new DeviceInUseException("Cannot update name when device is in use");
            }
            existing.setName(deviceDTO.getName());
        }
        if (deviceDTO.getBrand() != null) {
            if (existing.getState() == DeviceState.IN_USE) {
                logger.warn("Attempted to update brand of in-use device with id: {}", id);
                throw new DeviceInUseException("Cannot update brand when device is in use");
            }
            existing.setBrand(deviceDTO.getBrand());
        }
        if (deviceDTO.getState() != null) {
            existing.setState(deviceDTO.getState());
        }
        // creationTime not updated
        DeviceEntity saved = deviceRepository.save(existing);
        logger.info("Device partially updated successfully with id: {}", id);
        return mapToDto(saved);
    }

    public void deleteDevice(Long id) {
        logger.info("Deleting device with id: {}", id);
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Device not found with id: {} for deletion", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });
        if (entity.getState() == DeviceState.IN_USE) {
            logger.warn("Attempted to delete in-use device with id: {}", id);
            throw new DeviceInUseException("Cannot delete device in use");
        }
        deviceRepository.deleteById(id);
        logger.info("Device deleted successfully with id: {}", id);
    }

    public List<DeviceDTO> getDevicesByBrand(String brand) {
        logger.debug("Fetching devices by brand: {}", brand);
        return deviceRepository.findByBrand(brand).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<DeviceDTO> getDevicesByState(DeviceState state) {
        logger.debug("Fetching devices by state: {}", state);
        return deviceRepository.findByState(state).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DeviceDTO mapToDto(DeviceEntity entity) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBrand(entity.getBrand());
        dto.setState(entity.getState());
        dto.setCreationTime(entity.getCreationTime());
        return dto;
    }

    private DeviceEntity mapToEntity(DeviceDTO deviceDTO) {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(deviceDTO.getId());
        entity.setName(deviceDTO.getName());
        entity.setBrand(deviceDTO.getBrand());
        entity.setState(deviceDTO.getState());
        entity.setCreationTime(deviceDTO.getCreationTime());
        return entity;
    }
}
