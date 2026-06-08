package com.campus.lostfound.model;

import java.time.LocalDateTime;

public class Handover {
    private int handoverId;
    private int claimId;
    private int securityOfficerId;
    private LocalDateTime qrScannedAt;
    private String signaturePath;
    private boolean idVerified;
    private LocalDateTime handoverDate;
    private String remarks;

    public Handover() {}

    public Handover(int handoverId, int claimId, int securityOfficerId, LocalDateTime qrScannedAt, 
                    String signaturePath, boolean idVerified, LocalDateTime handoverDate, String remarks) {
        this.handoverId = handoverId;
        this.claimId = claimId;
        this.securityOfficerId = securityOfficerId;
        this.qrScannedAt = qrScannedAt;
        this.signaturePath = signaturePath;
        this.idVerified = idVerified;
        this.handoverDate = handoverDate;
        this.remarks = remarks;
    }

    public int getHandoverId() {
        return handoverId;
    }

    public void setHandoverId(int handoverId) {
        this.handoverId = handoverId;
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public int getSecurityOfficerId() {
        return securityOfficerId;
    }

    public void setSecurityOfficerId(int securityOfficerId) {
        this.securityOfficerId = securityOfficerId;
    }

    public LocalDateTime getQrScannedAt() {
        return qrScannedAt;
    }

    public void setQrScannedAt(LocalDateTime qrScannedAt) {
        this.qrScannedAt = qrScannedAt;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public boolean isIdVerified() {
        return idVerified;
    }

    public void setIdVerified(boolean idVerified) {
        this.idVerified = idVerified;
    }

    public LocalDateTime getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(LocalDateTime handoverDate) {
        this.handoverDate = handoverDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
