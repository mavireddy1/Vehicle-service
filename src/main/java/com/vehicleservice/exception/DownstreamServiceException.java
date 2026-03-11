package com.vehicleservice.exception;

/**
 * Thrown when a downstream service call fails or returns an unexpected response.
 */
public class DownstreamServiceException extends RuntimeException {

    public DownstreamServiceException(String serviceName, String detail) {
        super("Downstream service '" + serviceName + "' error: " + detail);
    }
}
