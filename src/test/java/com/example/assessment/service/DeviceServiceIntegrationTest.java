package com.example.assessment.service;

import com.example.assessment.config.TestcontainersConfiguration;
import com.example.assessment.exception.DeviceNotFoundException;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.entity.DeviceEntity;
import com.example.assessment.model.DeviceState;
import com.example.assessment.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DeviceServiceIntegrationTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
    }

    @Test
    void createDevice_shouldPersistAndReturnDevice() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Integration Test Device");
        deviceDTO.setBrand("Test Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceDTO result = deviceService.createDevice(deviceDTO);

        assertNotNull(result.getId());
        assertEquals("Integration Test Device", result.getName());
        assertEquals("Test Brand", result.getBrand());
        assertEquals(DeviceState.AVAILABLE, result.getState());
        assertNotNull(result.getCreationTime());

        // Verify in DB
        DeviceEntity entity = deviceRepository.findById(result.getId()).orElse(null);
        assertNotNull(entity);
        assertEquals("Integration Test Device", entity.getName());
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Test Device");
        deviceDTO.setBrand("Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceDTO created = deviceService.createDevice(deviceDTO);

        DeviceDTO result = deviceService.getDeviceById(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Test Device", result.getName());
    }

    @Test
    void getDeviceById_shouldThrowExceptionWhenNotFound() {
        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById(999L));
    }

    @Test
    void updateDevice_shouldUpdateAndPersist() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Old Device");
        deviceDTO.setBrand("Old Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceDTO created = deviceService.createDevice(deviceDTO);

        DeviceDTO updateDTO = new DeviceDTO();
        updateDTO.setName("Updated Device");
        updateDTO.setBrand("Updated Brand");
        updateDTO.setState(DeviceState.IN_USE);

        DeviceDTO result = deviceService.updateDevice(created.getId(), updateDTO);

        assertEquals("Updated Device", result.getName());
        assertEquals("Updated Brand", result.getBrand());
        assertEquals(DeviceState.IN_USE, result.getState());

        // Verify in DB
        DeviceEntity entity = deviceRepository.findById(created.getId()).orElse(null);
        assertNotNull(entity);
        assertEquals("Updated Device", entity.getName());
    }

    @Test
    void deleteDevice_shouldRemoveFromDB() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Device to Delete");
        deviceDTO.setBrand("Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceDTO created = deviceService.createDevice(deviceDTO);

        deviceService.deleteDevice(created.getId());

        assertFalse(deviceRepository.findById(created.getId()).isPresent());
    }

    @Test
    void getDevicesByBrand_shouldReturnFilteredList() {
        DeviceDTO device1 = new DeviceDTO();
        device1.setName("Device 1");
        device1.setBrand("Brand A");
        device1.setState(DeviceState.AVAILABLE);

        DeviceDTO device2 = new DeviceDTO();
        device2.setName("Device 2");
        device2.setBrand("Brand B");
        device2.setState(DeviceState.AVAILABLE);

        deviceService.createDevice(device1);
        deviceService.createDevice(device2);

        var result = deviceService.getDevicesByBrand("Brand A");

        assertEquals(1, result.size());
        assertEquals("Brand A", result.get(0).getBrand());
    }
}
