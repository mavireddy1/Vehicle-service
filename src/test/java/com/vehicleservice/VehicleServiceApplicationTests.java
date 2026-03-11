package com.vehicleservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "user-profile.service.url=http://localhost:8081",
        "identity.service.url=http://localhost:8082"
})
class VehicleServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts without errors.
    }
}
