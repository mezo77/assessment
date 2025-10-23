package com.example.assessment.service;

import com.example.assessment.exception.DeviceInUseException;
import com.example.assessment.exception.DeviceNotFoundException;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.entity.DeviceEntity;
import com.example.assessment.model.DeviceState;
import com.example.assessment.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void createDevice_shouldCreateAndReturnDevice() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Test Device");
        deviceDTO.setBrand("Test Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceEntity entity = new DeviceEntity();
        entity.setId(1L);
        entity.setName("Test Device");
        entity.setBrand("Test Brand");
        entity.setState(DeviceState.AVAILABLE);
        entity.setCreationTime(LocalDateTime.now());

        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(entity);

        DeviceDTO result = deviceService.createDevice(deviceDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Device", result.getName());
        verify(deviceRepository, times(1)).save(any(DeviceEntity.class));
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(1L);
        entity.setName("Test Device");
        entity.setState(DeviceState.AVAILABLE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(entity));

        DeviceDTO result = deviceService.getDeviceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDeviceById_shouldThrowExceptionWhenNotFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById(1L));
    }

    @Test
    void updateDevice_shouldUpdateAndReturnDevice() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Updated Device");
        deviceDTO.setBrand("Updated Brand");
        deviceDTO.setState(DeviceState.IN_USE);

        DeviceEntity existing = new DeviceEntity();
        existing.setId(1L);
        existing.setName("Old Device");
        existing.setBrand("Old Brand");
        existing.setState(DeviceState.AVAILABLE);
        existing.setCreationTime(LocalDateTime.now());

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(deviceRepository.save(any(DeviceEntity.class))).thenReturn(existing);

        DeviceDTO result = deviceService.updateDevice(1L, deviceDTO);

        assertNotNull(result);
        assertEquals("Updated Device", result.getName());
        verify(deviceRepository, times(1)).save(existing);
    }

    @Test
    void updateDevice_shouldThrowExceptionWhenDeviceInUse() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Updated Device");

        DeviceEntity existing = new DeviceEntity();
        existing.setId(1L);
        existing.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(DeviceInUseException.class, () -> deviceService.updateDevice(1L, deviceDTO));
    }

    @Test
    void deleteDevice_shouldDeleteWhenNotInUse() {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(1L);
        entity.setState(DeviceState.AVAILABLE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(entity));

        deviceService.deleteDevice(1L);

        verify(deviceRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDevice_shouldThrowExceptionWhenInUse() {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(1L);
        entity.setState(DeviceState.IN_USE);

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(DeviceInUseException.class, () -> deviceService.deleteDevice(1L));
    }
}
