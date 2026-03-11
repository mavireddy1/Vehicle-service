package com.vehicleservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a vehicle entity stored in the in-memory repository.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    private String id;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Owner user ID is required")
    private String ownerUserId;

    private String color;

    private String fuelType;

    private String vehicleType;
}
