package com.campus.lostfound.dao;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.ClaimStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AnalyticsDAO {
    private DatabaseConnection dbConnection;
    private ClaimDAOImpl claimDAO;

    public AnalyticsDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.claimDAO = new ClaimDAOImpl();
    }

    public int getTotalUsers() {
        String query = "SELECT total_users FROM dashboard_stats";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_users");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveLostItems() {
        String query = "SELECT active_lost FROM dashboard_stats";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("active_lost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAvailableFoundItems() {
        String query = "SELECT available_found FROM dashboard_stats";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("available_found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPendingClaims() {
        String query = "SELECT pending_claims FROM dashboard_stats";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("pending_claims");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getRecoveryRate() {
        String query = "SELECT " +
                       "  (SELECT COUNT(*) FROM lost_items WHERE status = 'recovered') as recovered, " +
                       "  (SELECT COUNT(*) FROM lost_items) as total";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double recovered = rs.getDouble("recovered");
                double total = rs.getDouble("total");
                if (total > 0) {
                    return (recovered / total) * 100.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Claim> getRecentClaims(int limit) {
        List<Claim> list = new ArrayList<>();
        String query = "SELECT * FROM claims ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(claimDAO.mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Integer> getLostItemCountByCategory() {
        Map<String, Integer> map = new HashMap<>();
        String query = "SELECT category, COUNT(*) as cnt FROM lost_items GROUP BY category";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<LocalDate, Integer> getDailyLostItems(LocalDate startDate, LocalDate endDate) {
        // Use TreeMap to maintain sorted order of dates
        Map<LocalDate, Integer> map = new TreeMap<>();
        String query = "SELECT date_lost, COUNT(*) as cnt FROM lost_items " +
                       "WHERE date_lost BETWEEN ? AND ? GROUP BY date_lost ORDER BY date_lost ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("date_lost");
                    if (date != null) {
                        map.put(date.toLocalDate(), rs.getInt("cnt"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
