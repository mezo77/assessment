package com.example.assessment.controller;

import com.example.assessment.exception.DeviceInUseException;
import com.example.assessment.exception.DeviceNotFoundException;
import com.example.assessment.model.DeviceDTO;
import com.example.assessment.model.DeviceState;
import com.example.assessment.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDevice_shouldReturnCreatedDevice() throws Exception {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setName("Test Device");
        deviceDTO.setBrand("Test Brand");
        deviceDTO.setState(DeviceState.AVAILABLE);

        DeviceDTO savedDevice = new DeviceDTO();
        savedDevice.setId(1L);
        savedDevice.setName("Test Device");
        savedDevice.setBrand("Test Brand");
        savedDevice.setState(DeviceState.AVAILABLE);
        savedDevice.setCreationTime(LocalDateTime.now());

        when(deviceService.createDevice(any(DeviceDTO.class))).thenReturn(savedDevice);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Device"));
    }

    @Test
    void getDevice_shouldReturnDevice() throws Exception {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(1L);
        deviceDTO.setName("Test Device");
        deviceDTO.setState(DeviceState.AVAILABLE);

        when(deviceService.getDeviceById(1L)).thenReturn(deviceDTO);

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Device"));
    }

    @Test
    void getDevice_shouldReturn404WhenNotFound() throws Exception {
        when(deviceService.getDeviceById(1L)).thenThrow(new DeviceNotFoundException("Device not found"));

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDevices_shouldReturnList() throws Exception {
        DeviceDTO device1 = new DeviceDTO();
        device1.setId(1L);
        device1.setName("Device 1");

        DeviceDTO device2 = new DeviceDTO();
        device2.setId(2L);
        device2.setName("Device 2");

        List<DeviceDTO> devices = Arrays.asList(device1, device2);

        when(deviceService.getAllDevices()).thenReturn(devices);

        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Device 1"));
    }

    @Test
    void updateDevice_shouldReturnUpdatedDevice() throws Exception {
        DeviceDTO updateDTO = new DeviceDTO();
        updateDTO.setName("Updated Device");
        updateDTO.setBrand("Updated Brand");
        updateDTO.setState(DeviceState.IN_USE);

        DeviceDTO updatedDevice = new DeviceDTO();
        updatedDevice.setId(1L);
        updatedDevice.setName("Updated Device");
        updatedDevice.setBrand("Updated Brand");
        updatedDevice.setState(DeviceState.IN_USE);

        when(deviceService.updateDevice(eq(1L), any(DeviceDTO.class))).thenReturn(updatedDevice);

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Device"));
    }

    @Test
    void updateDevice_shouldReturn400WhenInUse() throws Exception {
        DeviceDTO updateDTO = new DeviceDTO();
        updateDTO.setName("Updated Device");

        when(deviceService.updateDevice(eq(1L), any(DeviceDTO.class))).thenThrow(new DeviceInUseException("Cannot update name when device is in use"));

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDevice_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/devices/1"))
                .andExpect(status().isNoContent());
    }
}
