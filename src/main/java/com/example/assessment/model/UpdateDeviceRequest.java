package com.example.assessment.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDeviceRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    private DeviceState state;

    private LocalDateTime creationTime; // will be ignored if present
}

