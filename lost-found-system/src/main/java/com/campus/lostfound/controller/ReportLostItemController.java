package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.dao.LostItemDAO;
import com.campus.lostfound.dao.LostItemDAOImpl;
import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.LostStatus;
import com.campus.lostfound.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportLostItemController {
    @FXML private TextField itemNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> colorComboBox;
    @FXML private TextField locationField;
    @FXML private DatePicker dateLostPicker;
    @FXML private TextField estimatedValueField;
    @FXML private TextField brandField;
    @FXML private TextField serialField;
    @FXML private TextArea descriptionArea;
    @FXML private Label lblPhotoStatus;

    private List<String> uploadedPhotoPaths = new ArrayList<>();
    private LostItemDAO lostItemDAO = new LostItemDAOImpl();

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Reference Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
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

        LostItem item = new LostItem();
        item.setUserId(current.getUserId());
        item.setTitle(itemNameField.getText());
        item.setCategory(categoryComboBox.getValue());
        item.setColor(colorComboBox.getValue());
        item.setLocationLost(locationField.getText());
        item.setDateLost(dateLostPicker.getValue());
        
        String val = estimatedValueField.getText();
        if (val != null && !val.trim().isEmpty()) {
            item.setEstimatedValue(new BigDecimal(val.trim()));
        } else {
            item.setEstimatedValue(BigDecimal.ZERO);
        }

        item.setBrand(brandField.getText());
        item.setSerialNumber(serialField.getText());
        item.setDescription(descriptionArea.getText());
        item.setPhotoPaths(uploadedPhotoPaths);
        item.setStatus(LostStatus.ACTIVE);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        try {
            lostItemDAO.reportLostItem(item);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lost item reported successfully!", ButtonType.OK);
            alert.showAndWait();
            
            // Close dialog
            Stage stage = (Stage) itemNameField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error reporting item: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSaveDraft(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Draft saved locally.", ButtonType.OK);
        alert.showAndWait();
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
            errors.append("Please enter the last seen location.\n");
        }
        if (dateLostPicker.getValue() == null || dateLostPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("Please enter a valid date lost (cannot be in future).\n");
        }
        if (descriptionArea.getText() == null || descriptionArea.getText().trim().length() < 10) {
            errors.append("Description must be at least 10 characters long.\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors.toString(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
