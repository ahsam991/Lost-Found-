package com.campus.lostfound.dao;

import com.campus.lostfound.model.Role;
import com.campus.lostfound.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl extends BaseDAO<User> implements UserDAO {

    public UserDAOImpl() {
        super();
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setStudentId(rs.getString("student_id"));
        user.setDepartment(rs.getString("department"));
        user.setCampusLocation(rs.getString("campus_location"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setProfilePicturePath(rs.getString("profile_pic"));
        user.setVerified(rs.getBoolean("is_verified"));
        
        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        if (lastLoginTs != null) {
            user.setLastLogin(lastLoginTs.toLocalDateTime());
        }
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            user.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        return user;
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM users";
    }

    @Override
    public boolean register(User user) throws SQLException {
        String query = "INSERT INTO users (email, password_hash, full_name, phone, student_id, department, campus_location, role, profile_pic, is_verified, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getStudentId());
            stmt.setString(6, user.getDepartment());
            stmt.setString(7, user.getCampusLocation());
            stmt.setString(8, user.getRole().name().toLowerCase());
            stmt.setString(9, user.getProfilePicturePath());
            stmt.setBoolean(10, user.isVerified());
            stmt.setTimestamp(11, Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public User login(String email, String password) throws SQLException {
        User user = getUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
            // Update last login
            String updateQuery = "UPDATE users SET last_login = ? WHERE user_id = ?";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                LocalDateTime now = LocalDateTime.now();
                stmt.setTimestamp(1, Timestamp.valueOf(now));
                stmt.setInt(2, user.getUserId());
                stmt.executeUpdate();
                user.setLastLogin(now);
            }
            return user;
        }
        return null;
    }

    @Override
    public boolean forgotPassword(String email) throws SQLException {
        User user = getUserByEmail(email);
        return user != null;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) throws SQLException {
        User user = getUserByResetToken(token);
        if (user == null) {
            return false;
        }
        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String query = "UPDATE users SET password_hash = ?, reset_token = NULL, reset_token_expiry = NULL WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedNewPassword);
            stmt.setInt(2, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProfile(User user) throws SQLException {
        String query = "UPDATE users SET full_name = ?, phone = ?, student_id = ?, department = ?, campus_location = ?, profile_pic = ? WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getStudentId());
            stmt.setString(4, user.getDepartment());
            stmt.setString(5, user.getCampusLocation());
            stmt.setString(6, user.getProfilePicturePath());
            stmt.setInt(7, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        User user = getUserById(userId);
        if (user == null || !BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            return false;
        }
        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedNewPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByResetToken(String token) throws SQLException {
        String query = "SELECT * FROM users WHERE reset_token = ? AND reset_token_expiry > NOW()";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateResetToken(int userId, String token, LocalDateTime expiry) throws SQLException {
        String query = "UPDATE users SET reset_token = ?, reset_token_expiry = ? WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            stmt.setTimestamp(2, Timestamp.valueOf(expiry));
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return findAll();
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
