package com.example.assessment.controller;

import com.example.assessment.model.DeviceDTO;
import com.example.assessment.model.DeviceState;
import com.example.assessment.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO saved = deviceService.createDevice(deviceDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> deviceDTOS = deviceService.getAllDevices();
        return ResponseEntity.ok(deviceDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable Long id) {
        DeviceDTO deviceDTO = deviceService.getDeviceById(id);
        return ResponseEntity.ok(deviceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO updated = deviceService.updateDevice(id, deviceDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeviceDTO> partialUpdateDevice(@PathVariable Long id, @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO updated = deviceService.partialUpdateDevice(id, deviceDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByBrand(@PathVariable String brand) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByBrand(brand);
        return ResponseEntity.ok(deviceDTOS);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByState(@PathVariable DeviceState state) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByState(state);
        return ResponseEntity.ok(deviceDTOS);
    }
}
