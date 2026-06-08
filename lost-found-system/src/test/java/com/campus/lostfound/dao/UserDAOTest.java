package com.campus.lostfound.dao;

import com.campus.lostfound.model.Role;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.AuthService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDAOTest {

    @Test
    public void testAuthRegistrationValidator() {
        AuthService service = new AuthService(null, null);

        User validUser = new User();
        validUser.setEmail("student@university.edu");
        validUser.setStudentId("S12345");

        // Valid password
        assertTrue(service.validateRegistration(validUser, "SecurePass123"));

        // Password too short
        assertFalse(service.validateRegistration(validUser, "Sec1"));

        // No uppercase
        assertFalse(service.validateRegistration(validUser, "securepass123"));

        // No number
        assertFalse(service.validateRegistration(validUser, "SecurePass"));

        // Non-university email domain
        User invalidUser = new User();
        invalidUser.setEmail("student@gmail.com");
        invalidUser.setStudentId("S12345");
        assertFalse(service.validateRegistration(invalidUser, "SecurePass123"));
    }
}
