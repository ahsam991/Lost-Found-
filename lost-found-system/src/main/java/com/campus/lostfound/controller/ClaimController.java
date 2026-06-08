package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.ClaimStatus;
import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.ClaimService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimController {
    @FXML private Label lblItemTitle;
    @FXML private Label lblItemCategoryColor;
    @FXML private Label lblItemLocation;

    @FXML private TextField txtAnswer1;
    @FXML private TextArea txtAnswer2;
    @FXML private TextField txtAnswer3;
    @FXML private Label lblProofStatus;
    @FXML private Label lblErrorStatus;

    private FoundItem foundItem;
    private List<String> proofPaths = new ArrayList<>();
    private ClaimService claimService = new ClaimService();

    public void setFoundItem(FoundItem item) {
        this.foundItem = item;
        lblItemTitle.setText(item.getTitle());
        lblItemCategoryColor.setText(item.getCategory() + " / " + (item.getColor() != null ? item.getColor() : "Unspecified"));
        lblItemLocation.setText(item.getLocationFound());
    }

    @FXML
    private void handleUploadProof(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach Proof Document");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Document/Image Files", "*.pdf", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(lblItemTitle.getScene().getWindow());
        if (file != null) {
            proofPaths.add(file.getAbsolutePath());
            lblProofStatus.setText(file.getName() + " attached");
        }
    }

    @FXML
    private void handleSubmitClaim(ActionEvent event) {
        String ans1 = txtAnswer1.getText();
        String ans2 = txtAnswer2.getText();
        String ans3 = txtAnswer3.getText();

        if (ans1 == null || ans1.trim().isEmpty() || ans2 == null || ans2.trim().isEmpty()) {
            lblErrorStatus.setText("Please answer at least questions 1 & 2.");
            return;
        }

        User current = LostFoundApp.getCurrentUser();
        if (current == null || foundItem == null) return;

        Map<String, String> answers = new HashMap<>();
        answers.put("Where/When Lost", ans1);
        answers.put("Unique Markings", ans2);
        answers.put("Serial Number Provided", ans3);

        Claim claim = new Claim();
        claim.setClaimantId(current.getUserId());
        claim.setFoundItemId(foundItem.getFoundId());
        claim.setSecurityAnswers(answers);
        claim.setProofDocumentPaths(proofPaths);
        claim.setStatus(ClaimStatus.PENDING);
        claim.setCreatedAt(LocalDateTime.now());

        try {
            claimService.submitClaim(claim);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Verification claim submitted! Security will review it.", ButtonType.OK);
            alert.showAndWait();

            // Close dialog
            Stage stage = (Stage) lblItemTitle.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            lblErrorStatus.setText("Claim Submission Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) lblItemTitle.getScene().getWindow();
        stage.close();
    }
}
