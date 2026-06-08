package com.campus.lostfound.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LostItem extends BaseItem {
    private int lostId;
    private int userId;
    private String locationLost;
    private String locationLat;
    private String locationLng;
    private LocalDate dateLost;
    private BigDecimal estimatedValue;
    private List<String> photoPaths = new ArrayList<>();
    private LostStatus status = LostStatus.ACTIVE;
    private Double matchScore;
    private Integer matchedItemId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LostItem() {
        super();
    }

    public LostItem(String title, String category, String brand, String model, String color, String serialNumber, String description,
                    int lostId, int userId, String locationLost, String locationLat, String locationLng, LocalDate dateLost, 
                    BigDecimal estimatedValue, List<String> photoPaths, LostStatus status, Double matchScore, 
                    Integer matchedItemId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(title, category, brand, model, color, serialNumber, description);
        this.lostId = lostId;
        this.userId = userId;
        this.locationLost = locationLost;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.dateLost = dateLost;
        this.estimatedValue = estimatedValue;
        this.photoPaths = photoPaths != null ? photoPaths : new ArrayList<>();
        this.status = status != null ? status : LostStatus.ACTIVE;
        this.matchScore = matchScore;
        this.matchedItemId = matchedItemId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean isExpired() {
        return dateLost != null && dateLost.plusDays(30).isBefore(LocalDate.now());
    }

    public void markAsRecovered() {
        this.status = LostStatus.RECOVERED;
    }

    public int getLostId() {
        return lostId;
    }

    public void setLostId(int lostId) {
        this.lostId = lostId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLocationLost() {
        return locationLost;
    }

    public void setLocationLost(String locationLost) {
        this.locationLost = locationLost;
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

    public LocalDate getDateLost() {
        return dateLost;
    }

    public void setDateLost(LocalDate dateLost) {
        this.dateLost = dateLost;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(List<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public LostStatus getStatus() {
        return status;
    }

    public void setStatus(LostStatus status) {
        this.status = status;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public Integer getMatchedItemId() {
        return matchedItemId;
    }

    public void setMatchedItemId(Integer matchedItemId) {
        this.matchedItemId = matchedItemId;
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
