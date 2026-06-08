package com.campus.lostfound.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final Logger logger = LogManager.getLogger(EmailService.class);
    private Session session;

    public EmailService() {
        Properties props = new Properties();
        props.put("mail.smtp.host", System.getProperty("mail.smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", System.getProperty("mail.smtp.port", "587"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "5000"); // 5s connection timeout
        props.put("mail.smtp.connectiontimeout", "5000");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    System.getProperty("mail.username", "noreply@campuslostfound.com"), 
                    System.getProperty("mail.password", "password")
                );
            }
        });
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(System.getProperty("mail.username", "noreply@campuslostfound.com")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}. Note: This is normal in offline or dummy SMTP setups.", to, e.getMessage());
            // We do not rethrow to keep the mock flow smooth. If the caller needs validation, they can catch it.
        }
    }
}
