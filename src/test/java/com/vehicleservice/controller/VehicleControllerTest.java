package com.vehicleservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicleservice.exception.VehicleNotFoundException;
import com.vehicleservice.model.OtpResponse;
import com.vehicleservice.model.UserProfile;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.model.VehicleWithOwner;
import com.vehicleservice.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle sampleVehicle;

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
    }

    @Test
    void getAllVehicles_returnsListWithOk() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(List.of(sampleVehicle));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("v-001")));
    }

    @Test
    void getVehicleById_found_returnsVehicle() throws Exception {
        when(vehicleService.getVehicleById("v-001")).thenReturn(sampleVehicle);

        mockMvc.perform(get("/api/vehicles/v-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber", is("TS09AB1234")));
    }

    @Test
    void getVehicleById_notFound_returns404() throws Exception {
        when(vehicleService.getVehicleById("bad")).thenThrow(new VehicleNotFoundException("bad"));

        mockMvc.perform(get("/api/vehicles/bad"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addVehicle_validPayload_returnsCreated() throws Exception {
        when(vehicleService.addVehicle(any(Vehicle.class))).thenReturn(sampleVehicle);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleVehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("v-001")));
    }

    @Test
    void addVehicle_missingRequiredField_returns400() throws Exception {
        Vehicle invalid = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .year(2020)
                .ownerUserId("user-101")
                .build(); // registrationNumber is missing

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVehicle_validPayload_returnsOk() throws Exception {
        when(vehicleService.updateVehicle(eq("v-001"), any(Vehicle.class))).thenReturn(sampleVehicle);

        mockMvc.perform(put("/api/vehicles/v-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleVehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make", is("Toyota")));
    }

    @Test
    void deleteVehicle_exists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/vehicles/v-001"))
                .andExpect(status().isNoContent());

        verify(vehicleService).deleteVehicle("v-001");
    }

    @Test
    void deleteVehicle_notFound_returns404() throws Exception {
        doThrow(new VehicleNotFoundException("nope")).when(vehicleService).deleteVehicle("nope");

        mockMvc.perform(delete("/api/vehicles/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVehicleWithOwner_returnsEnrichedData() throws Exception {
        UserProfile owner = UserProfile.builder()
                .userId("user-101")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phoneNumber("+91-9876543210")
                .build();

        VehicleWithOwner combined = VehicleWithOwner.builder()
                .vehicle(sampleVehicle)
                .owner(owner)
                .build();

        when(vehicleService.getVehicleWithOwner("v-001")).thenReturn(combined);

        mockMvc.perform(get("/api/vehicles/v-001/owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.id", is("v-001")))
                .andExpect(jsonPath("$.owner.firstName", is("John")));
    }

    @Test
    void sendOtpToOwner_returnsOtpResponse() throws Exception {
        OtpResponse otpResponse = OtpResponse.builder()
                .referenceId("ref-999")
                .status("SENT")
                .message("OTP sent successfully")
                .maskedPhoneNumber("+91-XXXXXXX210")
                .build();

        when(vehicleService.sendOtpToVehicleOwner(eq("v-001"), anyString())).thenReturn(otpResponse);

        mockMvc.perform(post("/api/vehicles/v-001/send-otp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SENT")))
                .andExpect(jsonPath("$.referenceId", is("ref-999")));
    }
}
