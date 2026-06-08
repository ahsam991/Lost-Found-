package com.campus.lostfound.dao;

import com.campus.lostfound.model.Notification;
import com.campus.lostfound.model.NotificationType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl extends BaseDAO<Notification> implements NotificationDAO {

    public NotificationDAOImpl() {
        super();
    }

    @Override
    protected Notification mapResultSetToEntity(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setType(NotificationType.fromDbValue(rs.getString("type")));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        
        int relatedId = rs.getInt("related_id");
        if (!rs.wasNull()) {
            notification.setRelatedId(relatedId);
        }
        
        notification.setRead(rs.getBoolean("is_read"));
        notification.setEmailSent(rs.getBoolean("email_sent"));
        notification.setSmsSent(rs.getBoolean("sms_sent"));
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            notification.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        return notification;
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM notifications ORDER BY created_at DESC";
    }

    @Override
    public int create(Notification notification) throws SQLException {
        String query = "INSERT INTO notifications (user_id, type, title, message, related_id, is_read, email_sent, sms_sent, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getType().getDbValue());
            stmt.setString(3, notification.getTitle());
            stmt.setString(4, notification.getMessage());
            stmt.setInt(5, notification.getRelatedId());
            stmt.setBoolean(6, notification.isRead());
            stmt.setBoolean(7, notification.isEmailSent());
            stmt.setBoolean(8, notification.isSmsSent());
            stmt.setTimestamp(9, Timestamp.valueOf(notification.getCreatedAt() != null ? notification.getCreatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        notification.setNotificationId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Notification getById(int notificationId) throws SQLException {
        String query = "SELECT * FROM notifications WHERE notification_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Notification> getByUser(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean markAsRead(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markEmailSent(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET email_sent = TRUE WHERE notification_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markSmsSent(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET sms_sent = TRUE WHERE notification_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int getUnreadCount(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
