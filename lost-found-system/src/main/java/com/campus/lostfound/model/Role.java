package com.campus.lostfound.model;

public enum Role {
    STUDENT, STAFF, SECURITY, ADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return STUDENT;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
