package com.vehicleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Combines vehicle data with the owning user's profile for enriched responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleWithOwner {

    private Vehicle vehicle;
    private UserProfile owner;
}
