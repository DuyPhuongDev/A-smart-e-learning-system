package com.hcmut.lms.personalization.exception;

/**
 * Thrown when a required downstream service is unavailable,
 * making it impossible to complete the requested operation.
 */
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}