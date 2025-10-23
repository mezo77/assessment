package com.example.assessment.controller;

import com.example.assessment.model.*;
import com.example.assessment.service.DeviceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "APIs for managing devices (v1)")
public class DeviceController {

    private final DeviceService deviceService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @Operation(summary = "Create a new device", description = "Creates a new device resource")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device created",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class)))
    })
    public ResponseEntity<DeviceResponse> createDevice(
            @RequestBody(
                    description = "Create device payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateDeviceRequest.class),
                            examples = {@ExampleObject(value = "{\"name\":\"iPhone 16\",\"brand\":\"Apple\",\"state\":\"AVAILABLE\"}")}
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateDeviceRequest request) {
        DeviceDTO dto = new DeviceDTO();
        dto.setName(request.getName());
        dto.setBrand(request.getBrand());
        dto.setState(request.getState());
        DeviceDTO saved = deviceService.createDevice(dto);
        return ResponseEntity.ok(mapToResponse(saved));
    }

    @GetMapping
    @Operation(summary = "Get all devices", description = "Returns a pageable list of devices",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page index (0..)", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", example = "20"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sort, e.g. name,asc", example = "name,asc")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceResponse.class))))
    })
    public ResponseEntity<Page<DeviceResponse>> getAllDevices(Pageable pageable) {
        Page<DeviceDTO> page = deviceService.getAllDevices(pageable);
        org.springframework.data.domain.Page<DeviceResponse> mapped = page.map(this::mapToResponse);
        return ResponseEntity.ok(mapped);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a device by id", description = "Fetches a single device by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResponse> getDevice(@Parameter(description = "ID of the device", required = true) @PathVariable Long id) {
        DeviceDTO deviceDTO = deviceService.getDeviceById(id);
        return ResponseEntity.ok(mapToResponse(deviceDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a device", description = "Fully updates an existing device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResponse> updateDevice(@Parameter(description = "ID of the device to update", required = true) @PathVariable Long id,
                                                       @RequestBody(
                                                               description = "Full device payload for replace",
                                                               required = true,
                                                               content = @Content(mediaType = "application/json",
                                                                       schema = @Schema(implementation = UpdateDeviceRequest.class),
                                                                       examples = {@ExampleObject(value = "{\"name\":\"iPhone 16 Pro\",\"brand\":\"Apple\",\"state\":\"IN_USE\"}")}
                                                               )
                                                       )
                                                       @Valid @org.springframework.web.bind.annotation.RequestBody UpdateDeviceRequest request) {
        DeviceDTO dto = new DeviceDTO();
        dto.setName(request.getName());
        dto.setBrand(request.getBrand());
        dto.setState(request.getState());
        // ignore creationTime from request
        DeviceDTO updated = deviceService.updateDevice(id, dto);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a device", description = "Applies partial updates to a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device partially updated",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResponse> partialUpdateDevice(@Parameter(description = "ID of the device to patch", required = true) @PathVariable Long id,
                                                               @RequestBody(
                                                                       description = "Partial JSON payload. Only include fields to change. Example: {\"name\":\"New name\"}",
                                                                       required = true,
                                                                       content = @Content(mediaType = "application/json",
                                                                               schema = @Schema(implementation = CreateDeviceRequest.class),
                                                                               examples = {@ExampleObject(value = "{\"name\":\"iPhone 16 Mini\"}")}
                                                                       )
                                                               )
                                                               @org.springframework.web.bind.annotation.RequestBody JsonNode patch) {
        // Load existing DTO
        DeviceDTO existing = deviceService.getDeviceById(id);
        try {
            // Merge patch into existing DTO using Jackson
            DeviceDTO merged = objectMapper.readerForUpdating(existing).readValue(patch);
            // Prevent creationTime changes
            if (patch.has("creationTime")) {
                // reject
                throw new IllegalArgumentException("creationTime cannot be updated");
            }
            DeviceDTO updated = deviceService.partialUpdateDevice(id, merged);
            return ResponseEntity.ok(mapToResponse(updated));
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply patch", e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device", description = "Deletes a device by id if it is not in use")
    public ResponseEntity<Void> deleteDevice(@Parameter(description = "ID of the device to delete", required = true) @PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<DeviceResponse>> getDevicesByBrand(@Parameter(description = "Brand to filter by", required = true) @PathVariable String brand) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByBrand(brand);
        List<DeviceResponse> responses = deviceDTOS.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<DeviceResponse>> getDevicesByState(@Parameter(description = "State to filter by", required = true,
            schema = @Schema(implementation = DeviceState.class)) @PathVariable DeviceState state) {
        List<DeviceDTO> deviceDTOS = deviceService.getDevicesByState(state);
        List<DeviceResponse> responses = deviceDTOS.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private DeviceResponse mapToResponse(DeviceDTO dto) {
        DeviceResponse r = new DeviceResponse();
        r.setId(dto.getId());
        r.setName(dto.getName());
        r.setBrand(dto.getBrand());
        r.setState(dto.getState());
        r.setCreationTime(dto.getCreationTime());
        return r;
    }
}
