package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.dao.UserDAO;
import com.campus.lostfound.dao.UserDAOImpl;
import com.campus.lostfound.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;

public class ProfileController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtStudentId;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtLocation;

    @FXML private PasswordField txtOldPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    
    @FXML private Label lblAvatarStatus;
    @FXML private Label lblErrorSuccess;

    private UserDAO userDAO = new UserDAOImpl();
    private String avatarPath;

    @FXML
    public void initialize() {
        User current = LostFoundApp.getCurrentUser();
        if (current != null) {
            txtFullName.setText(current.getFullName());
            txtPhone.setText(current.getPhone());
            txtStudentId.setText(current.getStudentId());
            txtDepartment.setText(current.getDepartment());
            txtLocation.setText(current.getCampusLocation());
            avatarPath = current.getProfilePicturePath();
            if (avatarPath != null) {
                lblAvatarStatus.setText(new File(avatarPath).getName() + " loaded");
            }
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        User current = LostFoundApp.getCurrentUser();
        if (current == null) return;

        current.setFullName(txtFullName.getText());
        current.setPhone(txtPhone.getText());
        current.setStudentId(txtStudentId.getText());
        current.setDepartment(txtDepartment.getText());
        current.setCampusLocation(txtLocation.getText());
        current.setProfilePicturePath(avatarPath);

        try {
            boolean success = userDAO.updateProfile(current);
            if (success) {
                lblErrorSuccess.setStyle("-fx-text-fill: #006c49;");
                lblErrorSuccess.setText("Profile details updated successfully!");
            } else {
                lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
                lblErrorSuccess.setText("Failed to update profile details.");
            }
        } catch (Exception e) {
            lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
            lblErrorSuccess.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        User current = LostFoundApp.getCurrentUser();
        if (current == null) return;

        String oldPass = txtOldPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (oldPass == null || oldPass.trim().isEmpty() || newPass == null || newPass.trim().isEmpty()) {
            lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
            lblErrorSuccess.setText("Please enter both current and new passwords.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
            lblErrorSuccess.setText("New password confirmation does not match.");
            return;
        }

        try {
            boolean success = userDAO.changePassword(current.getUserId(), oldPass, newPass);
            if (success) {
                lblErrorSuccess.setStyle("-fx-text-fill: #006c49;");
                lblErrorSuccess.setText("Password changed successfully!");
                txtOldPassword.clear();
                txtNewPassword.clear();
                txtConfirmPassword.clear();
            } else {
                lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
                lblErrorSuccess.setText("Incorrect current password.");
            }
        } catch (Exception e) {
            lblErrorSuccess.setStyle("-fx-text-fill: #EF4444;");
            lblErrorSuccess.setText("Error updating password: " + e.getMessage());
        }
    }

    @FXML
    private void handleUploadAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(txtFullName.getScene().getWindow());
        if (file != null) {
            avatarPath = file.getAbsolutePath();
            lblAvatarStatus.setText(file.getName() + " selected");
        }
    }
}
