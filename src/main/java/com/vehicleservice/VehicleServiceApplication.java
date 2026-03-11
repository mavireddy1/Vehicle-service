package com.vehicleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Vehicle Service microservice.
 *
 * <p>This service manages vehicle data and integrates with:
 * <ul>
 *   <li>user-profile service – to fetch owner details for a given vehicle</li>
 *   <li>identity-service – to send OTP notifications to vehicle owners</li>
 * </ul>
 * The repository layer uses an in-memory {@link java.util.HashMap} as a
 * lightweight data store for demonstration purposes.
 */
@SpringBootApplication
public class VehicleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleServiceApplication.class, args);
    }
}
