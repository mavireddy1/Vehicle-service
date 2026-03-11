package com.vehicleservice.service;

import com.vehicleservice.client.IdentityServiceClient;
import com.vehicleservice.client.UserProfileClient;
import com.vehicleservice.exception.VehicleNotFoundException;
import com.vehicleservice.model.OtpRequest;
import com.vehicleservice.model.OtpResponse;
import com.vehicleservice.model.UserProfile;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.model.VehicleWithOwner;
import com.vehicleservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business-logic layer for vehicle operations.
 *
 * <p>Orchestrates calls to:
 * <ul>
 *   <li>{@link VehicleRepository} – in-memory HashMap store</li>
 *   <li>{@link UserProfileClient} – downstream user-profile service</li>
 *   <li>{@link IdentityServiceClient} – downstream identity-service for OTP</li>
 * </ul>
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserProfileClient userProfileClient;
    private final IdentityServiceClient identityServiceClient;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository,
                          UserProfileClient userProfileClient,
                          IdentityServiceClient identityServiceClient) {
        this.vehicleRepository = vehicleRepository;
        this.userProfileClient = userProfileClient;
        this.identityServiceClient = identityServiceClient;
    }

    /**
     * Return all vehicles in the store.
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Return a single vehicle by ID.
     *
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    /**
     * Return all vehicles owned by the given user.
     */
    public List<Vehicle> getVehiclesByOwner(String ownerUserId) {
        return vehicleRepository.findByOwnerUserId(ownerUserId);
    }

    /**
     * Add a new vehicle.
     */
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /**
     * Update an existing vehicle. Only mutable fields are overwritten.
     *
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public Vehicle updateVehicle(String id, Vehicle updated) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        existing.setRegistrationNumber(updated.getRegistrationNumber());
        existing.setMake(updated.getMake());
        existing.setModel(updated.getModel());
        existing.setYear(updated.getYear());
        existing.setOwnerUserId(updated.getOwnerUserId());
        existing.setColor(updated.getColor());
        existing.setFuelType(updated.getFuelType());
        existing.setVehicleType(updated.getVehicleType());

        return vehicleRepository.save(existing);
    }

    /**
     * Delete a vehicle by ID.
     *
     * @throws VehicleNotFoundException if no vehicle exists with the given ID
     */
    public void deleteVehicle(String id) {
        if (!vehicleRepository.deleteById(id)) {
            throw new VehicleNotFoundException(id);
        }
    }

    /**
     * Retrieve a vehicle together with its owner's profile from the
     * user-profile service.
     *
     * @throws VehicleNotFoundException   if the vehicle is not found
     * @throws com.vehicleservice.exception.DownstreamServiceException if the
     *         user-profile call fails
     */
    public VehicleWithOwner getVehicleWithOwner(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        UserProfile owner = userProfileClient.getUserProfile(vehicle.getOwnerUserId());

        return VehicleWithOwner.builder()
                .vehicle(vehicle)
                .owner(owner)
                .build();
    }

    /**
     * Send an OTP to the owner of the specified vehicle via the identity-service.
     *
     * @param vehicleId the vehicle whose owner should receive the OTP
     * @param purpose   a short description of the OTP purpose (e.g. "OWNERSHIP_TRANSFER")
     * @return {@link OtpResponse} from the identity-service
     * @throws VehicleNotFoundException   if the vehicle is not found
     * @throws com.vehicleservice.exception.DownstreamServiceException if the
     *         downstream calls fail
     */
    public OtpResponse sendOtpToVehicleOwner(String vehicleId, String purpose) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        UserProfile owner = userProfileClient.getUserProfile(vehicle.getOwnerUserId());

        OtpRequest otpRequest = OtpRequest.builder()
                .userId(owner.getUserId())
                .phoneNumber(owner.getPhoneNumber())
                .purpose(purpose)
                .build();

        return identityServiceClient.sendOtp(otpRequest);
    }
}
