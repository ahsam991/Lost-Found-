package com.campus.lostfound.service;

import com.campus.lostfound.dao.AnalyticsDAO;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.DashboardData;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnalyticsService {
    private AnalyticsDAO analyticsDAO;

    public AnalyticsService() {
        this.analyticsDAO = new AnalyticsDAO();
    }

    public AnalyticsService(AnalyticsDAO analyticsDAO) {
        this.analyticsDAO = analyticsDAO;
    }

    public DashboardData getDashboardData() {
        DashboardData data = new DashboardData();
        data.setTotalUsers(analyticsDAO.getTotalUsers());
        data.setActiveLostItems(analyticsDAO.getActiveLostItems());
        data.setAvailableFoundItems(analyticsDAO.getAvailableFoundItems());
        data.setRecoveryRate(analyticsDAO.getRecoveryRate());
        data.setPendingClaims(analyticsDAO.getPendingClaims());
        data.setRecentClaims(analyticsDAO.getRecentClaims(10));
        return data;
    }

    public Map<String, Integer> getCategoryStatistics() {
        return analyticsDAO.getLostItemCountByCategory();
    }

    public Map<LocalDate, Integer> getWeeklyTrend() {
        return analyticsDAO.getDailyLostItems(LocalDate.now().minusDays(30), LocalDate.now());
    }

    public byte[] generatePDFReport(String reportType, LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("SMART CAMPUS LOST & FOUND REPORT")
                .setFontSize(20)
                .setBold());
            document.add(new Paragraph("Report Type: " + reportType.toUpperCase()));
            document.add(new Paragraph("Period: " + startDate + " to " + endDate));
            document.add(new Paragraph("------------------------------------------------------------------"));

            // Core KPIs
            document.add(new Paragraph("System Statistics Summary:"));
            document.add(new Paragraph("- Total Users: " + analyticsDAO.getTotalUsers()));
            document.add(new Paragraph("- Active Lost Items: " + analyticsDAO.getActiveLostItems()));
            document.add(new Paragraph("- Available Found Items: " + analyticsDAO.getAvailableFoundItems()));
            document.add(new Paragraph("- Recovery Rate: " + String.format("%.2f%%", analyticsDAO.getRecoveryRate())));
            document.add(new Paragraph(" "));

            // Recent Claims Table
            document.add(new Paragraph("Recent Claims Listing:").setBold());
            
            float[] columnWidths = {100F, 100F, 150F, 150F};
            Table table = new Table(columnWidths);
            table.addCell(new Cell().add(new Paragraph("Claim ID").setBold()));
            table.addCell(new Cell().add(new Paragraph("Claimant ID").setBold()));
            table.addCell(new Cell().add(new Paragraph("Status").setBold()));
            table.addCell(new Cell().add(new Paragraph("Created At").setBold()));

            List<Claim> claims = analyticsDAO.getRecentClaims(20);
            for (Claim c : claims) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(c.getClaimId()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(c.getClaimantId()))));
                table.addCell(new Cell().add(new Paragraph(c.getStatus().name())));
                table.addCell(new Cell().add(new Paragraph(c.getCreatedAt() != null ? c.getCreatedAt().toString() : "-")));
            }
            
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
