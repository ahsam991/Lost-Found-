package com.campus.lostfound.dao;

import com.campus.lostfound.model.Handover;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HandoverDAOImpl extends BaseDAO<Handover> implements HandoverDAO {

    public HandoverDAOImpl() {
        super();
    }

    @Override
    protected Handover mapResultSetToEntity(ResultSet rs) throws SQLException {
        Handover handover = new Handover();
        handover.setHandoverId(rs.getInt("handover_id"));
        handover.setClaimId(rs.getInt("claim_id"));
        handover.setSecurityOfficerId(rs.getInt("security_officer_id"));
        
        Timestamp scannedTs = rs.getTimestamp("qr_scanned_at");
        if (scannedTs != null) {
            handover.setQrScannedAt(scannedTs.toLocalDateTime());
        }
        
        handover.setSignaturePath(rs.getString("signature_path"));
        handover.setIdVerified(rs.getBoolean("id_verified"));
        
        Timestamp handoverTs = rs.getTimestamp("handover_date");
        if (handoverTs != null) {
            handover.setHandoverDate(handoverTs.toLocalDateTime());
        }
        
        handover.setRemarks(rs.getString("remarks"));
        return handover;
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM handovers ORDER BY handover_date DESC";
    }

    @Override
    public int createHandover(Handover handover) throws SQLException {
        String query = "INSERT INTO handovers (claim_id, security_officer_id, qr_scanned_at, signature_path, id_verified, handover_date, remarks) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, handover.getClaimId());
            stmt.setInt(2, handover.getSecurityOfficerId());
            stmt.setTimestamp(3, handover.getQrScannedAt() != null ? Timestamp.valueOf(handover.getQrScannedAt()) : null);
            stmt.setString(4, handover.getSignaturePath());
            stmt.setBoolean(5, handover.isIdVerified());
            stmt.setTimestamp(6, Timestamp.valueOf(handover.getHandoverDate() != null ? handover.getHandoverDate() : LocalDateTime.now()));
            stmt.setString(7, handover.getRemarks());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        handover.setHandoverId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Handover getHandoverById(int handoverId) throws SQLException {
        String query = "SELECT * FROM handovers WHERE handover_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, handoverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Handover getHandoverByClaimId(int claimId) throws SQLException {
        String query = "SELECT * FROM handovers WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, claimId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Handover> getAllHandovers() throws SQLException {
        return findAll();
    }
}
