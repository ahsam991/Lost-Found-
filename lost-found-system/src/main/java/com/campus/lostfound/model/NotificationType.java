package com.campus.lostfound.model;

public enum NotificationType {
    MATCH_FOUND("match"),
    CLAIM_UPDATE("claim_update"),
    REMINDER("reminder"),
    SYSTEM_ALERT("system");

    private final String dbValue;

    NotificationType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static NotificationType fromDbValue(String dbValue) {
        if (dbValue == null) return SYSTEM_ALERT;
        for (NotificationType type : values()) {
            if (type.getDbValue().equalsIgnoreCase(dbValue) || type.name().equalsIgnoreCase(dbValue)) {
                return type;
            }
        }
        return SYSTEM_ALERT;
    }
}
