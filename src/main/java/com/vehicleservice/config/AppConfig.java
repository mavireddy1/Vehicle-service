package com.vehicleservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application-level Spring beans configuration.
 */
@Configuration
public class AppConfig {

    /**
     * Provides a shared {@link RestTemplate} instance used by downstream
     * service clients.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
