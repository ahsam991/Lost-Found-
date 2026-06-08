package com.campus.lostfound.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Claim {
    private int claimId;
    private int claimantId;
    private int foundItemId;
    private Integer lostItemId;
    private List<String> proofDocumentPaths = new ArrayList<>();
    private Map<String, String> securityAnswers = new HashMap<>();
    private String qrCodePath;
    private ClaimStatus status = ClaimStatus.PENDING;
    private String rejectionReason;
    private String verificationNotes;
    private Integer reviewedBy;
    private LocalDateTime reviewedAt;
    private Integer adminApprovedBy;
    private LocalDateTime adminApprovedAt;
    private LocalDateTime createdAt;

    public Claim() {}

    public Claim(int claimId, int claimantId, int foundItemId, Integer lostItemId, List<String> proofDocumentPaths, 
                 Map<String, String> securityAnswers, String qrCodePath, ClaimStatus status, String rejectionReason, 
                 String verificationNotes, Integer reviewedBy, LocalDateTime reviewedAt, Integer adminApprovedBy, 
                 LocalDateTime adminApprovedAt, LocalDateTime createdAt) {
        this.claimId = claimId;
        this.claimantId = claimantId;
        this.foundItemId = foundItemId;
        this.lostItemId = lostItemId;
        this.proofDocumentPaths = proofDocumentPaths != null ? proofDocumentPaths : new ArrayList<>();
        this.securityAnswers = securityAnswers != null ? securityAnswers : new HashMap<>();
        this.qrCodePath = qrCodePath;
        this.status = status != null ? status : ClaimStatus.PENDING;
        this.rejectionReason = rejectionReason;
        this.verificationNotes = verificationNotes;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.adminApprovedBy = adminApprovedBy;
        this.adminApprovedAt = adminApprovedAt;
        this.createdAt = createdAt;
    }

    public boolean isPending() {
        return status == ClaimStatus.PENDING;
    }

    public boolean canBeReviewed() {
        return status == ClaimStatus.PENDING || status == ClaimStatus.ADDITIONAL_INFO || status == ClaimStatus.REVIEW;
    }

    public void approve(int adminId) {
        this.status = ClaimStatus.APPROVED;
        this.adminApprovedBy = adminId;
        this.adminApprovedAt = LocalDateTime.now();
    }

    public void reject(String reason, int reviewerId) {
        this.status = ClaimStatus.REJECTED;
        this.rejectionReason = reason;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public int getClaimantId() {
        return claimantId;
    }

    public void setClaimantId(int claimantId) {
        this.claimantId = claimantId;
    }

    public int getFoundItemId() {
        return foundItemId;
    }

    public void setFoundItemId(int foundItemId) {
        this.foundItemId = foundItemId;
    }

    public Integer getLostItemId() {
        return lostItemId;
    }

    public void setLostItemId(Integer lostItemId) {
        this.lostItemId = lostItemId;
    }

    public List<String> getProofDocumentPaths() {
        return proofDocumentPaths;
    }

    public void setProofDocumentPaths(List<String> proofDocumentPaths) {
        this.proofDocumentPaths = proofDocumentPaths;
    }

    public Map<String, String> getSecurityAnswers() {
        return securityAnswers;
    }

    public void setSecurityAnswers(Map<String, String> securityAnswers) {
        this.securityAnswers = securityAnswers;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Integer getAdminApprovedBy() {
        return adminApprovedBy;
    }

    public void setAdminApprovedBy(Integer adminApprovedBy) {
        this.adminApprovedBy = adminApprovedBy;
    }

    public LocalDateTime getAdminApprovedAt() {
        return adminApprovedAt;
    }

    public void setAdminApprovedAt(LocalDateTime adminApprovedAt) {
        this.adminApprovedAt = adminApprovedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
