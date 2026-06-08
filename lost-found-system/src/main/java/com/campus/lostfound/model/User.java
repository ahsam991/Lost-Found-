package com.campus.lostfound.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String studentId;
    private String department;
    private String campusLocation;
    private Role role;
    private String profilePicturePath;
    private boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String email, String passwordHash, String fullName, String phone, 
                String studentId, String department, String campusLocation, Role role, 
                String profilePicturePath, boolean isVerified, LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.phone = phone;
        this.studentId = studentId;
        this.department = department;
        this.campusLocation = campusLocation;
        this.role = role;
        this.profilePicturePath = profilePicturePath;
        this.isVerified = isVerified;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCampusLocation() {
        return campusLocation;
    }

    public void setCampusLocation(String campusLocation) {
        this.campusLocation = campusLocation;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
