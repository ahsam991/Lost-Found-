package com.campus.lostfound.service;

import com.campus.lostfound.dao.UserDAO;
import com.campus.lostfound.dao.UserDAOImpl;
import com.campus.lostfound.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

public class AuthService {
    private UserDAO userDAO;
    private EmailService emailService;
    private static final SecureRandom secureRandom = new SecureRandom();

    public AuthService() {
        this.userDAO = new UserDAOImpl();
        this.emailService = new EmailService();
    }

    public AuthService(UserDAO userDAO, EmailService emailService) {
        this.userDAO = userDAO;
        this.emailService = emailService;
    }

    public boolean validateRegistration(User user, String plaintextPassword) {
        // Email must be university domain
        if (user.getEmail() == null || (!user.getEmail().endsWith(".edu") && !user.getEmail().contains("@university.edu") && !user.getEmail().contains("@campus.edu"))) {
            return false;
        }

        // Student ID format (optional validation, e.g. digits or simple check)
        if (user.getStudentId() != null && user.getStudentId().trim().isEmpty()) {
            return false;
        }

        // Password strength (min 8 chars, 1 uppercase, 1 number)
        if (plaintextPassword == null || plaintextPassword.length() < 8) {
            return false;
        }
        boolean hasUppercase = false;
        boolean hasDigit = false;
        for (char c : plaintextPassword.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUppercase && hasDigit;
    }

    public boolean register(User user, String plaintextPassword) throws SQLException {
        if (!validateRegistration(user, plaintextPassword)) {
            return false;
        }
        // BCrypt password hashing
        String hashed = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        user.setPasswordHash(hashed);
        user.setVerified(false); // requires email verification
        user.setCreatedAt(LocalDateTime.now());
        
        boolean success = userDAO.register(user);
        if (success) {
            sendVerificationEmail(user.getEmail());
        }
        return success;
    }

    public User authenticate(String email, String password) throws SQLException {
        return userDAO.login(email, password);
    }

    public String generateResetToken(int userId) throws SQLException {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        // Expiry in 1 hour
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        userDAO.updateResetToken(userId, token, expiry);
        return token;
    }

    public boolean forgotPassword(String email) throws SQLException {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            String token = generateResetToken(user.getUserId());
            // Send reset email asynchronously
            new Thread(() -> {
                try {
                    emailService.sendEmail(user.getEmail(), 
                        "Reset Password - Campus Lost & Found", 
                        "<h2>Password Reset Request</h2>" +
                        "<p>To reset your password, please click the link below or enter this token in the application:</p>" +
                        "<p><b>" + token + "</b></p>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            return true;
        }
        return false;
    }

    public boolean sendVerificationEmail(String email) {
        new Thread(() -> {
            try {
                emailService.sendEmail(email, 
                    "Verify Email - Campus Lost & Found", 
                    "<h2>Verify Your Account</h2>" +
                    "<p>Thank you for registering at Campus Lost & Found. Your email has been successfully registered.</p>" +
                    "<p>Please click here to verify your campus email address.</p>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true;
    }
}
