package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.FoundStatus;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.FoundItemService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportFoundItemController {
    @FXML private TextField itemNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> colorComboBox;
    @FXML private TextField locationField;
    @FXML private DatePicker dateFoundPicker;
    @FXML private TextField storageLocationField;
    @FXML private TextField brandField;
    @FXML private TextField serialField;
    @FXML private TextArea descriptionArea;
    @FXML private Label lblPhotoStatus;

    private List<String> uploadedPhotoPaths = new ArrayList<>();
    private FoundItemService foundItemService = new FoundItemService();

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(itemNameField.getScene().getWindow());
        if (selectedFile != null) {
            uploadedPhotoPaths.add(selectedFile.getAbsolutePath());
            lblPhotoStatus.setText(selectedFile.getName() + " attached");
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        User current = LostFoundApp.getCurrentUser();
        if (current == null) return;

        FoundItem item = new FoundItem();
        item.setUserId(current.getUserId());
        item.setTitle(itemNameField.getText());
        item.setCategory(categoryComboBox.getValue());
        item.setColor(colorComboBox.getValue());
        item.setLocationFound(locationField.getText());
        item.setDateFound(dateFoundPicker.getValue());
        item.setSafeKeepingLocation(storageLocationField.getText());
        item.setBrand(brandField.getText());
        item.setSerialNumber(serialField.getText());
        item.setDescription(descriptionArea.getText());
        item.setPhotoPaths(uploadedPhotoPaths);
        item.setStatus(FoundStatus.AVAILABLE);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        try {
            foundItemService.reportFoundItem(item);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Found item reported and auto-matching triggered!", ButtonType.OK);
            alert.showAndWait();

            Stage stage = (Stage) itemNameField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error reporting found item: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) itemNameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (itemNameField.getText() == null || itemNameField.getText().trim().length() < 3) {
            errors.append("Item name must be at least 3 characters.\n");
        }
        if (categoryComboBox.getValue() == null) {
            errors.append("Please select a category.\n");
        }
        if (locationField.getText() == null || locationField.getText().trim().isEmpty()) {
            errors.append("Please enter where the item was found.\n");
        }
        if (dateFoundPicker.getValue() == null || dateFoundPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("Please enter a valid date found.\n");
        }
        if (descriptionArea.getText() == null || descriptionArea.getText().trim().length() < 10) {
            errors.append("Description must be at least 10 characters.\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors.toString(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
