package com.campus.lostfound.controller;

import com.campus.lostfound.LostFoundApp;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.DashboardData;
import com.campus.lostfound.service.AnalyticsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Map;

public class AdminDashboardController {
    @FXML private Label totalUsersLabel;
    @FXML private Label lostItemsLabel;
    @FXML private Label foundItemsLabel;
    @FXML private Label pendingClaimsLabel;
    @FXML private Label recoveryRateLabel;

    @FXML private TableView<Claim> claimsTable;
    @FXML private TableColumn<Claim, String> colClaimId;
    @FXML private TableColumn<Claim, String> colClaimant;
    @FXML private TableColumn<Claim, String> colFoundItem;
    @FXML private TableColumn<Claim, String> colStatus;
    @FXML private TableColumn<Claim, String> colCreatedAt;

    @FXML private PieChart categoryChart;
    @FXML private LineChart<String, Number> trendChart;

    private AnalyticsService analyticsService = new AnalyticsService();

    @FXML
    public void initialize() {
        // Setup Columns
        colClaimId.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        colClaimant.setCellValueFactory(new PropertyValueFactory<>("claimantId"));
        colFoundItem.setCellValueFactory(new PropertyValueFactory<>("foundItemId"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        colCreatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCreatedAt() != null ? cellData.getValue().getCreatedAt().toString() : ""
        ));

        loadDashboardData();
        setupCharts();
    }

    private void loadDashboardData() {
        DashboardData data = analyticsService.getDashboardData();

        totalUsersLabel.setText(String.valueOf(data.getTotalUsers()));
        lostItemsLabel.setText(String.valueOf(data.getActiveLostItems()));
        foundItemsLabel.setText(String.valueOf(data.getAvailableFoundItems()));
        pendingClaimsLabel.setText(String.valueOf(data.getPendingClaims()));
        recoveryRateLabel.setText(String.format("%.1f%%", data.getRecoveryRate()));

        claimsTable.setItems(FXCollections.observableArrayList(data.getRecentClaims()));
    }

    private void setupCharts() {
        // Category Pie Chart
        Map<String, Integer> categoryStats = analyticsService.getCategoryStatistics();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        categoryChart.setData(pieData);

        // Trend Line Chart
        Map<LocalDate, Integer> trendData = analyticsService.getWeeklyTrend();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lost Items");

        for (Map.Entry<LocalDate, Integer> entry : trendData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        trendChart.getData().clear();
        trendChart.getData().add(series);
    }

    @FXML
    private void handleGeneratePDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.setInitialFileName("Campus_Lost_Found_Report.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        File file = fileChooser.showSaveDialog(LostFoundApp.getPrimaryStage());
        if (file != null) {
            try {
                byte[] pdfBytes = analyticsService.generatePDFReport(
                    "System Summary Report", 
                    LocalDate.now().minusDays(30), 
                    LocalDate.now()
                );
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "PDF Report exported successfully!", ButtonType.OK);
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save PDF: " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LostFoundApp.logout();
    }
}
