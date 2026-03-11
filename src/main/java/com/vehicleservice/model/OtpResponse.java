package com.vehicleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response received from the identity-service after triggering an OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {

    private String referenceId;
    private String status;
    private String message;
    private String maskedPhoneNumber;
}
