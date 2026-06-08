package com.campus.lostfound.model;

public enum ClaimStatus {
    PENDING, REVIEW, ADDITIONAL_INFO, APPROVED, REJECTED, COMPLETED;

    public static ClaimStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        try {
            return ClaimStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
