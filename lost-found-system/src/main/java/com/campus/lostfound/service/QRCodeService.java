package com.campus.lostfound.service;

import com.campus.lostfound.dao.*;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.User;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

public class QRCodeService {
    private static final String QR_CODE_DIR = "qrcodes/";
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    private ClaimDAO claimDAO;
    private UserDAO userDAO;
    private FoundItemDAO foundItemDAO;

    public QRCodeService() {
        this.claimDAO = new ClaimDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.foundItemDAO = new FoundItemDAOImpl();
    }

    public QRCodeService(ClaimDAO claimDAO, UserDAO userDAO, FoundItemDAO foundItemDAO) {
        this.claimDAO = claimDAO;
        this.userDAO = userDAO;
        this.foundItemDAO = foundItemDAO;
    }

    public String generateQRCode(int claimId) throws WriterException, IOException, SQLException {
        // Ensure folder exists
        File directory = new File(QR_CODE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String qrData = generateQRData(claimId);

        // Create QR code matrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
            qrData,
            BarcodeFormat.QR_CODE,
            WIDTH,
            HEIGHT
        );

        // Convert to buffered image
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Save to file
        String fileName = "claim_" + claimId + "_" + System.currentTimeMillis() + ".png";
        String filePath = QR_CODE_DIR + fileName;

        ImageIO.write(image, "PNG", new File(filePath));

        return filePath;
    }

    private String generateQRData(int claimId) throws SQLException {
        Claim claim = claimDAO.getClaimById(claimId);
        User claimant = userDAO.getUserById(claim.getClaimantId());
        FoundItem item = foundItemDAO.getFoundItemById(claim.getFoundItemId());

        // Create JSON payload
        JSONObject json = new JSONObject();
        json.put("claimId", claimId);
        json.put("itemName", item != null ? item.getTitle() : "Unknown Item");
        json.put("claimantName", claimant != null ? claimant.getFullName() : "Unknown Claimant");
        json.put("claimantId", claimant != null ? claimant.getStudentId() : "");
        json.put("timestamp", System.currentTimeMillis());
        json.put("hash", generateHash(claimId));

        return json.toString();
    }

    private String generateHash(int claimId) {
        String data = claimId + "SECRET_KEY";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyQRHash(int claimId, String receivedHash) {
        String expectedHash = generateHash(claimId);
        return expectedHash.equals(receivedHash);
    }

    // QR Code Scanner for Security
    public String scanQRCode(File qrImage) throws NotFoundException, IOException {
        BufferedImage image = ImageIO.read(qrImage);
        if (image == null) {
            throw new IOException("Failed to load image file: " + qrImage.getAbsolutePath());
        }
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
