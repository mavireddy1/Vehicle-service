package com.vehicleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload sent to the identity-service to trigger an OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    private String userId;
    private String phoneNumber;
    private String purpose;
}
