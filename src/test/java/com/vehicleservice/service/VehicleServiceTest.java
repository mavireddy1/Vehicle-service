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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserProfileClient userProfileClient;

    @Mock
    private IdentityServiceClient identityServiceClient;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle sampleVehicle;
    private UserProfile sampleUser;

    @BeforeEach
    void setUp() {
        sampleVehicle = Vehicle.builder()
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

        sampleUser = UserProfile.builder()
                .userId("user-101")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+91-9876543210")
                .build();
    }

    @Test
    void getAllVehicles_delegatesToRepository() {
        when(vehicleRepository.findAll()).thenReturn(List.of(sampleVehicle));
        List<Vehicle> result = vehicleService.getAllVehicles();
        assertThat(result).hasSize(1);
        verify(vehicleRepository).findAll();
    }

    @Test
    void getVehicleById_found_returnsVehicle() {
        when(vehicleRepository.findById("v-001")).thenReturn(Optional.of(sampleVehicle));
        Vehicle result = vehicleService.getVehicleById("v-001");
        assertThat(result.getId()).isEqualTo("v-001");
    }

    @Test
    void getVehicleById_notFound_throwsVehicleNotFoundException() {
        when(vehicleRepository.findById("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vehicleService.getVehicleById("bad"))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining("bad");
    }

    @Test
    void addVehicle_savesAndReturnsVehicle() {
        when(vehicleRepository.save(sampleVehicle)).thenReturn(sampleVehicle);
        Vehicle result = vehicleService.addVehicle(sampleVehicle);
        assertThat(result.getRegistrationNumber()).isEqualTo("TS09AB1234");
        verify(vehicleRepository).save(sampleVehicle);
    }

    @Test
    void deleteVehicle_exists_deletesSuccessfully() {
        when(vehicleRepository.deleteById("v-001")).thenReturn(true);
        vehicleService.deleteVehicle("v-001");
        verify(vehicleRepository).deleteById("v-001");
    }

    @Test
    void deleteVehicle_notFound_throwsVehicleNotFoundException() {
        when(vehicleRepository.deleteById("nope")).thenReturn(false);
        assertThatThrownBy(() -> vehicleService.deleteVehicle("nope"))
                .isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void getVehicleWithOwner_fetchesOwnerProfile() {
        when(vehicleRepository.findById("v-001")).thenReturn(Optional.of(sampleVehicle));
        when(userProfileClient.getUserProfile("user-101")).thenReturn(sampleUser);

        VehicleWithOwner result = vehicleService.getVehicleWithOwner("v-001");

        assertThat(result.getVehicle().getId()).isEqualTo("v-001");
        assertThat(result.getOwner().getFirstName()).isEqualTo("John");
        verify(userProfileClient).getUserProfile("user-101");
    }

    @Test
    void sendOtpToVehicleOwner_callsIdentityService() {
        OtpResponse expectedResponse = OtpResponse.builder()
                .referenceId("ref-123")
                .status("SENT")
                .message("OTP sent successfully")
                .build();

        when(vehicleRepository.findById("v-001")).thenReturn(Optional.of(sampleVehicle));
        when(userProfileClient.getUserProfile("user-101")).thenReturn(sampleUser);
        when(identityServiceClient.sendOtp(any(OtpRequest.class))).thenReturn(expectedResponse);

        OtpResponse result = vehicleService.sendOtpToVehicleOwner("v-001", "VEHICLE_VERIFICATION");

        assertThat(result.getStatus()).isEqualTo("SENT");
        assertThat(result.getReferenceId()).isEqualTo("ref-123");
        verify(identityServiceClient).sendOtp(any(OtpRequest.class));
    }
}
