package com.campus.lostfound.service;

import com.campus.lostfound.dao.NotificationDAO;
import com.campus.lostfound.dao.NotificationDAOImpl;
import com.campus.lostfound.dao.UserDAO;
import com.campus.lostfound.dao.UserDAOImpl;
import com.campus.lostfound.model.Notification;
import com.campus.lostfound.model.NotificationType;
import com.campus.lostfound.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);
    
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private EmailService emailService;
    private ExecutorService emailExecutor = Executors.newFixedThreadPool(5);
    private List<NotificationObserver> observers = new ArrayList<>();

    public NotificationService() {
        this.notificationDAO = new NotificationDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.emailService = new EmailService();
    }

    public NotificationService(NotificationDAO notificationDAO, UserDAO userDAO, EmailService emailService) {
        this.notificationDAO = notificationDAO;
        this.userDAO = userDAO;
        this.emailService = emailService;
    }

    public void sendNotification(int userId, NotificationType type, String title, String message, int relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedId(relatedId);
        notification.setRead(false);
        notification.setEmailSent(false);
        notification.setSmsSent(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Save to database
        try {
            int notificationId = notificationDAO.create(notification);
            notification.setNotificationId(notificationId);
        } catch (Exception e) {
            logger.error("Failed to persist notification: {}", e.getMessage());
        }

        // Send email asynchronously
        emailExecutor.submit(() -> {
            try {
                User user = userDAO.getUserById(userId);
                if (user != null && user.getEmail() != null) {
                    emailService.sendEmail(user.getEmail(), title, message);
                    notificationDAO.markEmailSent(notification.getNotificationId());
                }
            } catch (Exception e) {
                logger.error("Failed to send async notification email: {}", e.getMessage());
            }
        });

        // Notify observers (for real-time UI updates)
        synchronized (observers) {
            for (NotificationObserver observer : observers) {
                try {
                    observer.onNotification(notification);
                } catch (Exception e) {
                    logger.error("Observer notification callback error: {}", e.getMessage());
                }
            }
        }
    }

    public void sendBulkNotifications(List<Integer> userIds, NotificationType type, String title, String message) {
        // Process in parallel using parallel stream
        userIds.parallelStream().forEach(userId -> {
            sendNotification(userId, type, title, message, 0);
        });
    }

    public void sendNewClaimNotification(int claimId) {
        // Find security/admin users to notify them about a new claim
        try {
            List<User> users = userDAO.getAllUsers();
            for (User u : users) {
                if (u.getRole() == com.campus.lostfound.model.Role.SECURITY || u.getRole() == com.campus.lostfound.model.Role.ADMIN) {
                    sendNotification(u.getUserId(), NotificationType.CLAIM_UPDATE, 
                        "New Claim Registered", 
                        "Claim #" + claimId + " has been submitted and is pending verification.", 
                        claimId);
                }
            }
        } catch (Exception e) {
            logger.error("Error sending new claim notifications: {}", e.getMessage());
        }
    }

    public void sendClaimApprovedNotification(int claimantId, int claimId) {
        sendNotification(claimantId, NotificationType.CLAIM_UPDATE, 
            "Claim Approved!", 
            "Your claim #" + claimId + " has been approved by Security. A QR Code for pickup has been generated. Please check your dashboard.", 
            claimId);
    }

    public void sendAdditionalInfoRequest(int claimantId, int claimId, String notes) {
        sendNotification(claimantId, NotificationType.CLAIM_UPDATE, 
            "Additional Information Requested for Claim", 
            "Security requires additional information to verify your claim #" + claimId + ". Details: " + notes, 
            claimId);
    }

    public void addObserver(NotificationObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(NotificationObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public void shutdown() {
        emailExecutor.shutdown();
    }
}
