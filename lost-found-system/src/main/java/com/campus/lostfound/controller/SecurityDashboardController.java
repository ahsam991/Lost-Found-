package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.dao.*;
import com.campus.lostfound.model.*;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.service.QRCodeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class SecurityDashboardController {
    @FXML private TableView<Claim> tblClaimsQueue;
    @FXML private TableColumn<Claim, String> colQueueId;
    @FXML private TableColumn<Claim, String> colQueueClaimant;
    @FXML private TableColumn<Claim, String> colQueueItem;
    @FXML private TableColumn<Claim, String> colQueueStatus;

    @FXML private VBox paneVerificationDetails;
    @FXML private Label lblDetailId;
    @FXML private Label lblDetailClaimant;
    @FXML private Label lblDetailItemTitle;
    @FXML private Label lblDetailProof;
    @FXML private TextArea txtDetailAnswers;
    @FXML private TextField txtOfficerNotes;

    @FXML private Label lblScanStatus;
    @FXML private Button btnCompleteHandover;

    private ClaimDAO claimDAO = new ClaimDAOImpl();
    private FoundItemDAO foundItemDAO = new FoundItemDAOImpl();
    private HandoverDAO handoverDAO = new HandoverDAOImpl();
    private ClaimService claimService = new ClaimService();
    private QRCodeService qrCodeService = new QRCodeService();

    private Claim selectedClaim;
    private int scannedClaimId = -1;

    @FXML
    public void initialize() {
        colQueueId.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        colQueueClaimant.setCellValueFactory(new PropertyValueFactory<>("claimantId"));
        colQueueStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        
        colQueueItem.setCellValueFactory(cellData -> {
            try {
                FoundItem item = foundItemDAO.getFoundItemById(cellData.getValue().getFoundItemId());
                return new SimpleStringProperty(item != null ? item.getTitle() : "Unknown");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        // Add Listener for Row Selection
        tblClaimsQueue.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showClaimDetails(newVal);
            }
        });

        loadQueue();
    }

    private void loadQueue() {
        try {
            List<Claim> list = claimDAO.getClaimsByStatus(ClaimStatus.PENDING);
            list.addAll(claimDAO.getClaimsByStatus(ClaimStatus.REVIEW));
            list.addAll(claimDAO.getClaimsByStatus(ClaimStatus.ADDITIONAL_INFO));

            tblClaimsQueue.setItems(FXCollections.observableArrayList(list));
            paneVerificationDetails.setDisable(true);
            clearDetails();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearDetails() {
        lblDetailId.setText("-");
        lblDetailClaimant.setText("-");
        lblDetailItemTitle.setText("-");
        lblDetailProof.setText("-");
        txtDetailAnswers.setText("");
        txtOfficerNotes.setText("");
        selectedClaim = null;
    }

    private void showClaimDetails(Claim claim) {
        selectedClaim = claim;
        paneVerificationDetails.setDisable(false);

        lblDetailId.setText(String.valueOf(claim.getClaimId()));
        lblDetailClaimant.setText(String.valueOf(claim.getClaimantId()));
        
        try {
            FoundItem item = foundItemDAO.getFoundItemById(claim.getFoundItemId());
            lblDetailItemTitle.setText(item != null ? item.getTitle() : "Unknown");
        } catch (SQLException e) {
            lblDetailItemTitle.setText("Error");
        }

        lblDetailProof.setText(claim.getProofDocumentPaths().isEmpty() ? "No files uploaded" : 
                               claim.getProofDocumentPaths().size() + " files attached");

        StringBuilder answers = new StringBuilder();
        claim.getSecurityAnswers().forEach((k, v) -> {
            answers.append("Q: ").append(k).append("\nA: ").append(v).append("\n\n");
        });
        txtDetailAnswers.setText(answers.toString());
        txtOfficerNotes.setText(claim.getVerificationNotes());
    }

    @FXML
    private void handleApproveClaim(ActionEvent event) {
        if (selectedClaim == null) return;
        String notes = txtOfficerNotes.getText();
        User officer = LostFoundApp.getCurrentUser();
        int officerId = officer != null ? officer.getUserId() : 1;

        try {
            boolean approved = claimService.verifyClaim(selectedClaim.getClaimId(), notes, officerId);
            if (approved) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Claim approved successfully! QR pass has been sent to claimant.", ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Claim automatically flagged. Score was below verification threshold.", ButtonType.OK);
                alert.showAndWait();
            }
            loadQueue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRequestInfo(ActionEvent event) {
        if (selectedClaim == null) return;
        String notes = txtOfficerNotes.getText();
        if (notes == null || notes.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please specify notes requesting additional info.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        User officer = LostFoundApp.getCurrentUser();
        int officerId = officer != null ? officer.getUserId() : 1;

        try {
            claimDAO.updateClaimStatus(selectedClaim.getClaimId(), ClaimStatus.ADDITIONAL_INFO, officerId);
            claimDAO.updateVerificationNotes(selectedClaim.getClaimId(), notes);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Claim status updated to ADDITIONAL_INFO and claimant notified.", ButtonType.OK);
            alert.showAndWait();
            loadQueue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleScanQRFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open QR Code Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.png, *.jpg)", "*.png", "*.jpg"));
        
        File file = fileChooser.showOpenDialog(LostFoundApp.getPrimaryStage());
        if (file != null) {
            try {
                String decodedJson = qrCodeService.scanQRCode(file);
                JSONObject json = new JSONObject(decodedJson);
                int claimId = json.getInt("claimId");
                String hash = json.getString("hash");

                if (qrCodeService.verifyQRHash(claimId, hash)) {
                    scannedClaimId = claimId;
                    lblScanStatus.setText("QR Pass Valid! Claim ID: #" + claimId + " | Claimant: " + json.getString("claimantName"));
                    btnCompleteHandover.setDisable(false);
                } else {
                    scannedClaimId = -1;
                    lblScanStatus.setText("Error: QR Code Signature Hash Verification Failed!");
                    btnCompleteHandover.setDisable(true);
                }
            } catch (Exception e) {
                lblScanStatus.setText("Error: Failed to decode QR Code. " + e.getMessage());
                btnCompleteHandover.setDisable(true);
            }
        }
    }

    @FXML
    private void handleCompleteHandover(ActionEvent event) {
        if (scannedClaimId == -1) return;
        User officer = LostFoundApp.getCurrentUser();
        int officerId = officer != null ? officer.getUserId() : 1;

        try {
            Claim claim = claimDAO.getClaimById(scannedClaimId);
            if (claim != null) {
                // Save physical Handover record
                Handover handover = new Handover();
                handover.setClaimId(scannedClaimId);
                handover.setSecurityOfficerId(officerId);
                handover.setQrScannedAt(LocalDateTime.now());
                handover.setIdVerified(true);
                handover.setHandoverDate(LocalDateTime.now());
                handover.setRemarks("Pickup complete. Claimant verified physically by Officer.");
                
                handoverDAO.createHandover(handover);

                // Update Claim & Found Item Statuses to COMPLETED / RETURNED
                claimDAO.updateClaimStatus(scannedClaimId, ClaimStatus.COMPLETED, officerId);
                foundItemDAO.updateFoundItemStatusAndClaimant(claim.getFoundItemId(), FoundStatus.RETURNED, claim.getClaimantId());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Handover completed successfully! Found Item marked as returned.", ButtonType.OK);
                alert.showAndWait();

                lblScanStatus.setText("No QR code scanned yet.");
                btnCompleteHandover.setDisable(true);
                scannedClaimId = -1;
                loadQueue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenScanner(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Select a QR code image to simulate scanning with our built-in reader.", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LostFoundApp.logout();
    }
}
