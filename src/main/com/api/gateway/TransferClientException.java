package com.api.gateway;

import com.api.common.ErrorCode;

public class TransferClientException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;
    private final ErrorCode code;

    public TransferClientException(String message, ErrorCode code) {
        super(code.getDescription() + "\n" + message);
        this.code = code;
    }

}
