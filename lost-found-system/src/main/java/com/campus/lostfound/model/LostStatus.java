package com.campus.lostfound.model;

public enum LostStatus {
    ACTIVE, MATCHED, RECOVERED, CLOSED, EXPIRED;

    public static LostStatus fromString(String statusStr) {
        if (statusStr == null) return ACTIVE;
        try {
            return LostStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
