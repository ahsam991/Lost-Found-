package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.dao.*;
import com.campus.lostfound.matching.MatchResult;
import com.campus.lostfound.matching.MatchingEngine;
import com.campus.lostfound.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StudentDashboardController {
    @FXML private Label lblUserName;
    @FXML private Label lblLostCount;
    @FXML private Label lblFoundCount;
    @FXML private Label lblPendingCount;

    @FXML private TextField searchField;
    
    @FXML private TableView<FoundItem> tblFoundItems;
    @FXML private TableColumn<FoundItem, String> colTitle;
    @FXML private TableColumn<FoundItem, String> colCategory;
    @FXML private TableColumn<FoundItem, String> colColor;
    @FXML private TableColumn<FoundItem, String> colLocation;
    @FXML private TableColumn<FoundItem, String> colDate;

    @FXML private ListView<String> lstMatches;

    @FXML private TableView<LostItem> tblMyLostReports;
    @FXML private TableColumn<LostItem, String> colMyLostTitle;
    @FXML private TableColumn<LostItem, String> colMyLostCategory;
    @FXML private TableColumn<LostItem, String> colMyLostLocation;
    @FXML private TableColumn<LostItem, String> colMyLostDate;
    @FXML private TableColumn<LostItem, String> colMyLostStatus;

    private LostItemDAO lostItemDAO = new LostItemDAOImpl();
    private FoundItemDAO foundItemDAO = new FoundItemDAOImpl();
    private ClaimDAO claimDAO = new ClaimDAOImpl();
    private MatchingEngine matchingEngine = new MatchingEngine();

    @FXML
    public void initialize() {
        User current = LostFoundApp.getCurrentUser();
        if (current != null) {
            lblUserName.setText("Welcome, " + current.getFullName() + "!");
        }

        // Configure Columns
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("locationFound"));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateFound().toString()));

        colMyLostTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMyLostCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colMyLostLocation.setCellValueFactory(new PropertyValueFactory<>("locationLost"));
        colMyLostDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateLost().toString()));
        colMyLostStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        refreshData();
    }

    private void refreshData() {
        User current = LostFoundApp.getCurrentUser();
        if (current == null) return;

        try {
            // Stats counts
            List<LostItem> myLostList = lostItemDAO.getLostItemsByUser(current.getUserId());
            List<FoundItem> availableList = foundItemDAO.getAllAvailableFoundItems();
            List<Claim> myClaims = claimDAO.getClaimsByUser(current.getUserId());

            lblLostCount.setText(String.valueOf(myLostList.size()));
            lblFoundCount.setText(String.valueOf(availableList.size()));
            
            long pendingClaimsCount = myClaims.stream().filter(c -> c.isPending() || c.getStatus() == ClaimStatus.REVIEW).count();
            lblPendingCount.setText(String.valueOf(pendingClaimsCount));

            // Populate Tables
            tblFoundItems.setItems(FXCollections.observableArrayList(availableList));
            tblMyLostReports.setItems(FXCollections.observableArrayList(myLostList));

            // Smart Recommendation Matches
            lstMatches.getItems().clear();
            for (LostItem lost : myLostList) {
                if (lost.getStatus() == LostStatus.ACTIVE) {
                    List<MatchResult> matches = matchingEngine.findMatchesForLostItem(lost);
                    for (MatchResult match : matches) {
                        lstMatches.getItems().add(
                            "Lost: " + lost.getTitle() + " <-> Found: " + match.getFoundItem().getTitle() + 
                            " [" + match.getConfidenceLevel() + " Conf: " + Math.round(match.getMatchScore() * 100) + "%]"
                        );
                    }
                }
            }
            if (lstMatches.getItems().isEmpty()) {
                lstMatches.getItems().add("No active matches found. We will notify you if a match occurs!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText();
        try {
            List<FoundItem> results = foundItemDAO.searchFoundItems(keyword, null, null);
            tblFoundItems.setItems(FXCollections.observableArrayList(results));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fileClaimForSelected(ActionEvent event) {
        FoundItem selected = tblFoundItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a found item to claim.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        openClaimDialog(selected);
        refreshData();
    }

    private void openClaimDialog(FoundItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(LostFoundApp.class.getResource("/fxml/claim_item.fxml"));
            Parent root = loader.load();

            ClaimController controller = loader.getController();
            controller.setFoundItem(item);

            Stage stage = new Stage();
            stage.setTitle("Submit Verification Claim");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(LostFoundApp.getPrimaryStage());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LostFoundApp.class.getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboardView(ActionEvent event) {
        refreshData();
    }

    @FXML
    private void showReportLostView(ActionEvent event) {
        openModal("/fxml/report_lost_item.fxml", "Report Lost Item");
        refreshData();
    }

    @FXML
    private void showReportFoundView(ActionEvent event) {
        openModal("/fxml/report_found_item.fxml", "Report Found Item");
        refreshData();
    }

    @FXML
    private void showClaimsView(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "File claims by selecting items from the table and clicking 'File Claim'. Check notifications for updates.", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void showProfileView(ActionEvent event) {
        openModal("/fxml/profile.fxml", "My Profile");
    }

    private void openModal(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(LostFoundApp.class.getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(LostFoundApp.getPrimaryStage());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(LostFoundApp.class.getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LostFoundApp.logout();
    }
}
