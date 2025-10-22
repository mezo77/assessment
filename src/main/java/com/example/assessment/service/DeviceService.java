package com.example.assessment.service;

import com.example.assessment.exception.DeviceInUseException;
import com.example.assessment.exception.DeviceNotFoundException;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.model.DeviceEntity;
import com.example.assessment.model.DeviceState;
import com.example.assessment.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceDTO createDevice(DeviceDTO deviceDTO) {
        DeviceEntity entity = mapToEntity(deviceDTO);
        entity.setCreationTime(LocalDateTime.now());
        DeviceEntity saved = deviceRepository.save(entity);
        return mapToDto(saved);
    }

    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DeviceDTO getDeviceById(Long id) {
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        return mapToDto(entity);
    }

    public DeviceDTO updateDevice(Long id, DeviceDTO deviceDTO) {
        DeviceEntity existing = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        if (deviceDTO.getName() != null && existing.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseException("Cannot update name when device is in use");
        }
        if (deviceDTO.getBrand() != null && existing.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseException("Cannot update brand when device is in use");
        }
        existing.setName(deviceDTO.getName());
        existing.setBrand(deviceDTO.getBrand());
        existing.setState(deviceDTO.getState());
        // creationTime not updated
        DeviceEntity saved = deviceRepository.save(existing);
        return mapToDto(saved);
    }

    public DeviceDTO partialUpdateDevice(Long id, DeviceDTO deviceDTO) {
        DeviceEntity existing = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        if (deviceDTO.getName() != null) {
            if (existing.getState() == DeviceState.IN_USE) {
                throw new DeviceInUseException("Cannot update name when device is in use");
            }
            existing.setName(deviceDTO.getName());
        }
        if (deviceDTO.getBrand() != null) {
            if (existing.getState() == DeviceState.IN_USE) {
                throw new DeviceInUseException("Cannot update brand when device is in use");
            }
            existing.setBrand(deviceDTO.getBrand());
        }
        if (deviceDTO.getState() != null) {
            existing.setState(deviceDTO.getState());
        }
        // creationTime not updated
        DeviceEntity saved = deviceRepository.save(existing);
        return mapToDto(saved);
    }

    public void deleteDevice(Long id) {
        DeviceEntity entity = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        if (entity.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseException("Cannot delete device in use");
        }
        deviceRepository.deleteById(id);
    }

    public List<DeviceDTO> getDevicesByBrand(String brand) {
        return deviceRepository.findByBrand(brand).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<DeviceDTO> getDevicesByState(DeviceState state) {
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
