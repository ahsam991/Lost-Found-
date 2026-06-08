package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;
    @FXML private Hyperlink forgotPasswordLink;

    private AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        try {
            User user = authService.authenticate(email, password);
            if (user != null) {
                errorLabel.setText("");
                LostFoundApp.showDashboard(user);
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        } catch (Exception e) {
            errorLabel.setText("Database connectivity error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = emailField.getText();
        if (email == null || email.trim().isEmpty()) {
            errorLabel.setText("Please enter your email address first.");
            return;
        }
        try {
            boolean success = authService.forgotPassword(email);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                    "Password reset token sent to your email! (Please check terminal logs)", ButtonType.OK);
                alert.showAndWait();
            } else {
                errorLabel.setText("Email address not found.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(LostFoundApp.class.getResource("/fxml/register.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(LostFoundApp.class.getResource("/css/styles.css").toExternalForm());
            LostFoundApp.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
