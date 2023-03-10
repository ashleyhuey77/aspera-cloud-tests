package com.api.common;

public enum ErrorCode {
    STATUS_WAIT_TIMEOUT(0, "A timeout occurred while waiting for the status of the transfer to be complete."),
    TRANSFER_ERROR(1, "An error occurred while calling the transfer api."),
    TRANSFER_STATUS_ERROR(2, "An error occurred while calling the transfer status api."),
    REQUEST_BODY_ERROR(3, "There was an error retrieving the request body."),
    CONFIGURATION_ERROR(4, "There was an error with the config file.");

    private final int code;
    private final String description;

    private ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}

