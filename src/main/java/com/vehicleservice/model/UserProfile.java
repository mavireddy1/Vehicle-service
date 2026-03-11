package com.vehicleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user profile fetched from the downstream user-profile service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}
