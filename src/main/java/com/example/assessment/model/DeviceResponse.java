package com.example.assessment.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceResponse {
    private Long id;
    private String name;
    private String brand;
    private DeviceState state;
    private LocalDateTime creationTime;
}

