package com.example.assessment.controller;

import com.example.assessment.model.DeviceDTO;
import com.example.assessment.model.DeviceState;
import com.example.assessment.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Added missing OpenAPI imports
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "APIs for managing devices")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @Operation(summary = "Create a new device", description = "Creates a new device resource")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device created",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class)))
    })
    public ResponseEntity<DeviceDTO> createDevice(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Device payload", required = true,
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))) @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO saved = deviceService.createDevice(deviceDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @Operation(summary = "Get all devices", description = "Returns a list of all devices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceDTO.class))))
    })
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> deviceDTOS = deviceService.getAllDevices();
        return ResponseEntity.ok(deviceDTOS);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a device by id", description = "Fetches a single device by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceDTO> getDevice(@Parameter(description = "ID of the device", required = true) @PathVariable Long id) {
        DeviceDTO deviceDTO = deviceService.getDeviceById(id);
        return ResponseEntity.ok(deviceDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a device", description = "Fully updates an existing device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceDTO> updateDevice(@Parameter(description = "ID of the device to update", required = true) @PathVariable Long id, @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Device payload", required = true,
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))) @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO updated = deviceService.updateDevice(id, deviceDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a device", description = "Applies partial updates to a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device partially updated",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceDTO> partialUpdateDevice(@Parameter(description = "ID of the device to patch", required = true) @PathVariable Long id, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Partial device payload", required = true,
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))) @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO updated = deviceService.partialUpdateDevice(id, deviceDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device", description = "Deletes a device by id if it is not in use")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<Void> deleteDevice(@Parameter(description = "ID of the device to delete", required = true) @PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get devices by brand", description = "Fetches devices filtered by brand")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceDTO.class))))
    })
    public ResponseEntity<List<DeviceDTO>> getDevicesByBrand(@Parameter(description = "Brand to filter by", required = true) @PathVariable String brand) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByBrand(brand);
        return ResponseEntity.ok(deviceDTOS);
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "Get devices by state", description = "Fetches devices filtered by state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceDTO.class))))
    })
    public ResponseEntity<List<DeviceDTO>> getDevicesByState(@Parameter(description = "State to filter by", required = true,
            schema = @Schema(implementation = DeviceState.class)) @PathVariable DeviceState state) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByState(state);
        return ResponseEntity.ok(deviceDTOS);
    }
}
