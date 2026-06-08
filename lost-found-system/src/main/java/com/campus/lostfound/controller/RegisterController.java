package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.model.Role;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField studentIdField;
    @FXML private TextField phoneField;
    @FXML private TextField departmentField;
    @FXML private TextField locationField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    private AuthService authService = new AuthService();

    @FXML
    private void handleRegister(ActionEvent event) {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String studentId = studentIdField.getText();
        String phone = phoneField.getText();
        String department = departmentField.getText();
        String campusLocation = locationField.getText();
        String password = passwordField.getText();
        String roleStr = roleComboBox.getValue();

        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            studentId == null || studentId.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            roleStr == null) {
            errorLabel.setText("Please fill in all required fields (*).");
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setStudentId(studentId);
        user.setPhone(phone);
        user.setDepartment(department);
        user.setCampusLocation(campusLocation);
        user.setRole(Role.fromString(roleStr));
        user.setCreatedAt(LocalDateTime.now());

        try {
            boolean success = authService.register(user, password);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                    "Registration successful! Verification email has been sent. Please log in.", ButtonType.OK);
                alert.showAndWait();
                goToLogin(null);
            } else {
                errorLabel.setText("Registration failed. Ensure email ends with .edu and password is >= 8 characters with 1 uppercase and 1 number.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        LostFoundApp.showLoginScreen();
    }
}
