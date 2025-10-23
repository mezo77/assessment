package com.example.assessment.controller;

import com.example.assessment.config.TestcontainersConfiguration;
import com.example.assessment.entity.DeviceEntity;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.model.DeviceState;
import com.example.assessment.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
    }

    @Test
    void createDevice_shouldReturnCreatedDevice() throws Exception {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Integration Device");
        deviceDTO.setBrand("Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Device"))
                .andExpect(jsonPath("$.brand").value("Brand"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.creationTime").exists());
    }

    @Test
    void getDevice_shouldReturnDevice() throws Exception {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Test Device");
        deviceDTO.setBrand("Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        // Create device via service or directly
        var entity = new DeviceEntity();
        entity.setName("Test Device");
        entity.setBrand("Brand");
        entity.setState(DeviceState.AVAILABLE);
        entity.setCreationTime(java.time.LocalDateTime.now());
        entity = deviceRepository.save(entity);

        mockMvc.perform(get("/api/v1/devices/" + entity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()))
                .andExpect(jsonPath("$.name").value("Test Device"));
    }

    @Test
    void getDevice_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/devices/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDevices_shouldReturnList() throws Exception {
        var entity1 = new DeviceEntity();
        entity1.setName("Device 1");
        entity1.setBrand("Brand");
        entity1.setState(DeviceState.AVAILABLE);
        entity1.setCreationTime(java.time.LocalDateTime.now());
        deviceRepository.save(entity1);

        var entity2 = new DeviceEntity();
        entity2.setName("Device 2");
        entity2.setBrand("Brand");
        entity2.setState(DeviceState.AVAILABLE);
        entity2.setCreationTime(java.time.LocalDateTime.now());
        deviceRepository.save(entity2);

        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Device 1"))
                .andExpect(jsonPath("$.content[1].name").value("Device 2"));
    }

    @Test
    void updateDevice_shouldReturnUpdatedDevice() throws Exception {
        var entity = new DeviceEntity();
        entity.setName("Old Device");
        entity.setBrand("Old Brand");
        entity.setState(DeviceState.AVAILABLE);
        entity.setCreationTime(java.time.LocalDateTime.now());
        entity = deviceRepository.save(entity);

        DeviceDTO updateDTO = new DeviceDTO();
        updateDTO.setName("Updated Device");
        updateDTO.setBrand("Updated Brand");
        updateDTO.setState(DeviceState.IN_USE);

        mockMvc.perform(put("/api/v1/devices/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Device"))
                .andExpect(jsonPath("$.brand").value("Updated Brand"))
                .andExpect(jsonPath("$.state").value("IN_USE"));
    }

    @Test
    void deleteDevice_shouldReturnNoContent() throws Exception {
        var entity = new DeviceEntity();
        entity.setName("Device to Delete");
        entity.setBrand("Brand");
        entity.setState(DeviceState.AVAILABLE);
        entity.setCreationTime(java.time.LocalDateTime.now());
        entity = deviceRepository.save(entity);

        mockMvc.perform(delete("/api/v1/devices/" + entity.getId()))
                .andExpect(status().isNoContent());

        // Verify deleted
        assert !deviceRepository.findById(entity.getId()).isPresent();
    }

    @Test
    void getDevicesByBrand_shouldReturnFilteredList() throws Exception {
        var entity1 = new DeviceEntity();
        entity1.setName("Device 1");
        entity1.setBrand("Brand A");
        entity1.setState(DeviceState.AVAILABLE);
        entity1.setCreationTime(java.time.LocalDateTime.now());
        deviceRepository.save(entity1);

        var entity2 = new DeviceEntity();
        entity2.setName("Device 2");
        entity2.setBrand("Brand B");
        entity2.setState(DeviceState.AVAILABLE);
        entity2.setCreationTime(java.time.LocalDateTime.now());
        deviceRepository.save(entity2);

        mockMvc.perform(get("/api/v1/devices/brand/Brand A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Brand A"));
    }
}
