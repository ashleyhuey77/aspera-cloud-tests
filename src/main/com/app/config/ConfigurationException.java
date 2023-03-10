package com.app.config;

import com.api.common.ErrorCode;

public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;
    private final ErrorCode code;

    public ConfigurationException(String message, ErrorCode code) {
        super(code.getDescription() + "\n" + message);
        this.code = code;
    }
}
