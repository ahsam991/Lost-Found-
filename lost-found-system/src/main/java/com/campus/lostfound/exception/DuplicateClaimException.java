package com.campus.lostfound.exception;

public class DuplicateClaimException extends RuntimeException {
    public DuplicateClaimException(String message) {
        super(message);
    }
}
