package com.vehicleservice.exception;

/**
 * Thrown when a requested vehicle is not found in the repository.
 */
public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String vehicleId) {
        super("Vehicle not found with id: " + vehicleId);
    }
}
