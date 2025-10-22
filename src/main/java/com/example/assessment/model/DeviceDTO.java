package com.example.assessment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    @NotNull
    private DeviceState state;

    private LocalDateTime creationTime;

}
