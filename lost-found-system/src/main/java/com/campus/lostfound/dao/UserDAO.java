package com.campus.lostfound.dao;

import com.campus.lostfound.model.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface UserDAO {
    boolean register(User user) throws SQLException;
    User login(String email, String password) throws SQLException;
    boolean forgotPassword(String email) throws SQLException;
    boolean resetPassword(String token, String newPassword) throws SQLException;
    boolean updateProfile(User user) throws SQLException;
    boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException;
    User getUserById(int userId) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
    User getUserByResetToken(String token) throws SQLException;
    boolean updateResetToken(int userId, String token, LocalDateTime expiry) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    boolean deleteUser(int userId) throws SQLException;
}
