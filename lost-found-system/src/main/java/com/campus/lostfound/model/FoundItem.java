package com.campus.lostfound.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FoundItem extends BaseItem {
    private int foundId;
    private int userId;
    private String locationFound;
    private String locationLat;
    private String locationLng;
    private LocalDate dateFound;
    private String safeKeepingLocation;
    private List<String> photoPaths = new ArrayList<>();
    private FoundStatus status = FoundStatus.AVAILABLE;
    private Integer claimedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FoundItem() {
        super();
    }

    public FoundItem(String title, String category, String brand, String model, String color, String serialNumber, String description,
                     int foundId, int userId, String locationFound, String locationLat, String locationLng, LocalDate dateFound, 
                     String safeKeepingLocation, List<String> photoPaths, FoundStatus status, Integer claimedById, 
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(title, category, brand, model, color, serialNumber, description);
        this.foundId = foundId;
        this.userId = userId;
        this.locationFound = locationFound;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.dateFound = dateFound;
        this.safeKeepingLocation = safeKeepingLocation;
        this.photoPaths = photoPaths != null ? photoPaths : new ArrayList<>();
        this.status = status != null ? status : FoundStatus.AVAILABLE;
        this.claimedById = claimedById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isAvailable() {
        return status == FoundStatus.AVAILABLE;
    }

    public void markAsClaimed(int claimantId) {
        this.claimedById = claimantId;
        this.status = FoundStatus.CLAIMED;
    }

    public int getFoundId() {
        return foundId;
    }

    public void setFoundId(int foundId) {
        this.foundId = foundId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public void setLocationFound(String locationFound) {
        this.locationFound = locationFound;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(String locationLng) {
        this.locationLng = locationLng;
    }

    public LocalDate getDateFound() {
        return dateFound;
    }

    public void setDateFound(LocalDate dateFound) {
        this.dateFound = dateFound;
    }

    public String getSafeKeepingLocation() {
        return safeKeepingLocation;
    }

    public void setSafeKeepingLocation(String safeKeepingLocation) {
        this.safeKeepingLocation = safeKeepingLocation;
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(List<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public FoundStatus getStatus() {
        return status;
    }

    public void setStatus(FoundStatus status) {
        this.status = status;
    }

    public Integer getClaimedById() {
        return claimedById;
    }

    public void setClaimedById(Integer claimedById) {
        this.claimedById = claimedById;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
