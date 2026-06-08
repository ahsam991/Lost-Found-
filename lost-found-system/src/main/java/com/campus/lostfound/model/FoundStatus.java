package com.campus.lostfound.model;

public enum FoundStatus {
    AVAILABLE, VERIFICATION, CLAIMED, RETURNED, ARCHIVED;

    public static FoundStatus fromString(String statusStr) {
        if (statusStr == null) return AVAILABLE;
        try {
            return FoundStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AVAILABLE;
        }
    }
}
