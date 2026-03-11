package com.vehicleservice.repository;

import com.vehicleservice.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory vehicle repository backed by a {@link HashMap}.
 * Pre-loaded with sample data at application startup.
 */
@Repository
public class VehicleRepository {

    private final Map<String, Vehicle> vehicleStore = new HashMap<>();

    public VehicleRepository() {
        initSampleData();
    }

    /**
     * Retrieve all vehicles.
     */
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicleStore.values());
    }

    /**
     * Find a vehicle by its ID.
     */
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehicleStore.get(id));
    }

    /**
     * Find all vehicles belonging to a specific owner.
     */
    public List<Vehicle> findByOwnerUserId(String ownerUserId) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : vehicleStore.values()) {
            if (ownerUserId.equals(v.getOwnerUserId())) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     * Save (create or update) a vehicle. Generates a new ID if not present.
     */
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        vehicleStore.put(vehicle.getId(), vehicle);
        return vehicle;
    }

    /**
     * Delete a vehicle by ID. Returns {@code true} if the vehicle existed.
     */
    public boolean deleteById(String id) {
        return vehicleStore.remove(id) != null;
    }

    /**
     * Check whether a vehicle with the given ID exists.
     */
    public boolean existsById(String id) {
        return vehicleStore.containsKey(id);
    }

    // ------------------------------------------------------------------ //
    //  Sample data                                                         //
    // ------------------------------------------------------------------ //

    private void initSampleData() {
        Vehicle v1 = Vehicle.builder()
                .id("v-001")
                .registrationNumber("TS09AB1234")
                .make("Toyota")
                .model("Corolla")
                .year(2020)
                .ownerUserId("user-101")
                .color("White")
                .fuelType("Petrol")
                .vehicleType("Sedan")
                .build();

        Vehicle v2 = Vehicle.builder()
                .id("v-002")
                .registrationNumber("TS10CD5678")
                .make("Honda")
                .model("Civic")
                .year(2019)
                .ownerUserId("user-102")
                .color("Blue")
                .fuelType("Petrol")
                .vehicleType("Sedan")
                .build();

        Vehicle v3 = Vehicle.builder()
                .id("v-003")
                .registrationNumber("AP05EF9012")
                .make("Hyundai")
                .model("Creta")
                .year(2022)
                .ownerUserId("user-101")
                .color("Black")
                .fuelType("Diesel")
                .vehicleType("SUV")
                .build();

        Vehicle v4 = Vehicle.builder()
                .id("v-004")
                .registrationNumber("MH12GH3456")
                .make("Maruti Suzuki")
                .model("Swift")
                .year(2021)
                .ownerUserId("user-103")
                .color("Red")
                .fuelType("Petrol")
                .vehicleType("Hatchback")
                .build();

        vehicleStore.put(v1.getId(), v1);
        vehicleStore.put(v2.getId(), v2);
        vehicleStore.put(v3.getId(), v3);
        vehicleStore.put(v4.getId(), v4);
    }
}
