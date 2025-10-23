package com.example.assessment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeviceRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    @NotNull
    private DeviceState state;
}

