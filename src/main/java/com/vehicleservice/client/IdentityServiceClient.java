package com.vehicleservice.client;

import com.vehicleservice.exception.DownstreamServiceException;
import com.vehicleservice.model.OtpRequest;
import com.vehicleservice.model.OtpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for the downstream <b>identity-service</b>.
 *
 * <p>Sends OTP (One-Time Password) requests to vehicle owners via the
 * identity-service. The base URL is configured via the
 * {@code identity.service.url} application property.
 */
@Component
public class IdentityServiceClient {

    private final RestTemplate restTemplate;
    private final String identityServiceUrl;

    public IdentityServiceClient(RestTemplate restTemplate,
                                 @Value("${identity.service.url}") String identityServiceUrl) {
        this.restTemplate = restTemplate;
        this.identityServiceUrl = identityServiceUrl;
    }

    /**
     * Request that the identity-service sends an OTP to the specified owner.
     *
     * @param otpRequest the OTP request payload (userId, phoneNumber, purpose)
     * @return {@link OtpResponse} containing the reference ID and delivery status
     * @throws DownstreamServiceException if the call to identity-service fails
     */
    public OtpResponse sendOtp(OtpRequest otpRequest) {
        String url = identityServiceUrl + "/api/otp/send";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OtpRequest> request = new HttpEntity<>(otpRequest, headers);

            OtpResponse response = restTemplate.postForObject(url, request, OtpResponse.class);
            if (response == null) {
                throw new DownstreamServiceException("identity-service",
                        "Empty OTP response for userId: " + otpRequest.getUserId());
            }
            return response;
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("identity-service",
                    "Failed to send OTP: " + ex.getMessage());
        }
    }
}
