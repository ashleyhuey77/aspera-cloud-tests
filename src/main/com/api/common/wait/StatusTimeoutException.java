package com.api.common.wait;

import com.api.common.ErrorCode;

public class StatusTimeoutException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;
    private final ErrorCode code;

    public StatusTimeoutException(String message, ErrorCode code) {
        super(code.getDescription() + "\n" + message);
        this.code = code;
    }
}
