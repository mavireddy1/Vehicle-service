package com.vehicleservice.controller;

import com.vehicleservice.model.OtpResponse;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.model.VehicleWithOwner;
import com.vehicleservice.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing vehicle-related endpoints.
 *
 * <p>Base path: {@code /api/vehicles}
 *
 * <h3>Endpoint summary</h3>
 * <ul>
 *   <li>GET    /api/vehicles              – list all vehicles</li>
 *   <li>GET    /api/vehicles/{id}         – get vehicle by ID</li>
 *   <li>GET    /api/vehicles/owner/{userId} – list vehicles by owner</li>
 *   <li>POST   /api/vehicles              – add a new vehicle</li>
 *   <li>PUT    /api/vehicles/{id}         – update a vehicle</li>
 *   <li>DELETE /api/vehicles/{id}         – delete a vehicle</li>
 *   <li>GET    /api/vehicles/{id}/owner   – get vehicle + owner profile (user-profile service)</li>
 *   <li>POST   /api/vehicles/{id}/send-otp – send OTP to vehicle owner (identity-service)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Retrieve all vehicles.
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    /**
     * Retrieve a single vehicle by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * Retrieve all vehicles belonging to a specific owner.
     */
    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByOwner(@PathVariable String ownerUserId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(ownerUserId));
    }

    /**
     * Add a new vehicle.
     */
    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@Valid @RequestBody Vehicle vehicle) {
        Vehicle saved = vehicleService.addVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Update an existing vehicle.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable String id,
                                                  @Valid @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    /**
     * Delete a vehicle by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a vehicle together with the owner's profile from the
     * downstream <b>user-profile</b> service.
     */
    @GetMapping("/{id}/owner")
    public ResponseEntity<VehicleWithOwner> getVehicleWithOwner(@PathVariable String id) {
        return ResponseEntity.ok(vehicleService.getVehicleWithOwner(id));
    }

    /**
     * Send an OTP to the vehicle owner via the downstream
     * <b>identity-service</b>.
     *
     * @param id      the vehicle ID whose owner should receive the OTP
     * @param purpose optional purpose for the OTP (defaults to "VEHICLE_VERIFICATION")
     */
    @PostMapping("/{id}/send-otp")
    public ResponseEntity<OtpResponse> sendOtpToOwner(
            @PathVariable String id,
            @RequestParam(defaultValue = "VEHICLE_VERIFICATION") String purpose) {
        return ResponseEntity.ok(vehicleService.sendOtpToVehicleOwner(id, purpose));
    }
}
