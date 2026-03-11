package com.vehicleservice.repository;

import com.vehicleservice.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleRepositoryTest {

    private VehicleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new VehicleRepository();
    }

    @Test
    void findAll_returnsSampleData() {
        List<Vehicle> vehicles = repository.findAll();
        assertThat(vehicles).isNotEmpty();
        assertThat(vehicles).hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    void findById_existingId_returnsVehicle() {
        Optional<Vehicle> result = repository.findById("v-001");
        assertThat(result).isPresent();
        assertThat(result.get().getRegistrationNumber()).isEqualTo("TS09AB1234");
    }

    @Test
    void findById_missingId_returnsEmpty() {
        Optional<Vehicle> result = repository.findById("does-not-exist");
        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerUserId_returnsMatchingVehicles() {
        List<Vehicle> result = repository.findByOwnerUserId("user-101");
        assertThat(result).hasSize(2);
        result.forEach(v -> assertThat(v.getOwnerUserId()).isEqualTo("user-101"));
    }

    @Test
    void save_newVehicle_assignsId() {
        Vehicle vehicle = Vehicle.builder()
                .registrationNumber("KA01AA0001")
                .make("Tesla")
                .model("Model 3")
                .year(2023)
                .ownerUserId("user-200")
                .build();

        Vehicle saved = repository.save(vehicle);

        assertThat(saved.getId()).isNotBlank();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void save_existingVehicle_updatesEntry() {
        Vehicle vehicle = repository.findById("v-001").orElseThrow();
        vehicle.setColor("Silver");
        repository.save(vehicle);

        assertThat(repository.findById("v-001").get().getColor()).isEqualTo("Silver");
    }

    @Test
    void deleteById_existingId_removesEntry() {
        boolean deleted = repository.deleteById("v-002");
        assertThat(deleted).isTrue();
        assertThat(repository.findById("v-002")).isEmpty();
    }

    @Test
    void deleteById_missingId_returnsFalse() {
        boolean deleted = repository.deleteById("ghost-id");
        assertThat(deleted).isFalse();
    }

    @Test
    void existsById_returnsCorrectly() {
        assertThat(repository.existsById("v-003")).isTrue();
        assertThat(repository.existsById("nope")).isFalse();
    }
}
