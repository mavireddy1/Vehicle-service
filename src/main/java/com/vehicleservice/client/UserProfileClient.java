package com.vehicleservice.client;

import com.vehicleservice.exception.DownstreamServiceException;
import com.vehicleservice.model.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for the downstream <b>user-profile</b> service.
 *
 * <p>Retrieves user-profile details (name, email, phone, etc.) by user ID.
 * The base URL is configured via the {@code user-profile.service.url}
 * application property.
 */
@Component
public class UserProfileClient {

    private final RestTemplate restTemplate;
    private final String userProfileServiceUrl;

    public UserProfileClient(RestTemplate restTemplate,
                             @Value("${user-profile.service.url}") String userProfileServiceUrl) {
        this.restTemplate = restTemplate;
        this.userProfileServiceUrl = userProfileServiceUrl;
    }

    /**
     * Fetch the user profile for the given {@code userId}.
     *
     * @param userId the unique identifier of the user
     * @return {@link UserProfile} populated from the user-profile service
     * @throws DownstreamServiceException if the call fails or the user is not found
     */
    public UserProfile getUserProfile(String userId) {
        String url = userProfileServiceUrl + "/api/users/" + userId;
        try {
            UserProfile profile = restTemplate.getForObject(url, UserProfile.class);
            if (profile == null) {
                throw new DownstreamServiceException("user-profile",
                        "Empty response for userId: " + userId);
            }
            return profile;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new DownstreamServiceException("user-profile",
                    "User not found for userId: " + userId);
        } catch (RestClientException ex) {
            throw new DownstreamServiceException("user-profile",
                    "Failed to call user-profile service: " + ex.getMessage());
        }
    }
}
