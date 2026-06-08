package com.campus.lostfound.model;

import java.util.ArrayList;
import java.util.List;

public class DashboardData {
    private int totalUsers;
    private int activeLostItems;
    private int availableFoundItems;
    private double recoveryRate;
    private int pendingClaims;
    private List<Claim> recentClaims = new ArrayList<>();

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getActiveLostItems() {
        return activeLostItems;
    }

    public void setActiveLostItems(int activeLostItems) {
        this.activeLostItems = activeLostItems;
    }

    public int getAvailableFoundItems() {
        return availableFoundItems;
    }

    public void setAvailableFoundItems(int availableFoundItems) {
        this.availableFoundItems = availableFoundItems;
    }

    public double getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(int pendingClaims) {
        this.pendingClaims = pendingClaims;
    }

    public List<Claim> getRecentClaims() {
        return recentClaims;
    }

    public void setRecentClaims(List<Claim> recentClaims) {
        this.recentClaims = recentClaims;
    }
}
