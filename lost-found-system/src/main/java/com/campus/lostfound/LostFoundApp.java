package com.campus.lostfound;

import com.campus.lostfound.model.Role;
import com.campus.lostfound.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LostFoundApp extends Application {
    private static Stage primaryStage;
    private static User currentUser;
    private static Scene loginScene;
    private static Scene dashboardScene;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Smart Campus Lost & Found System");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        // Load login screen
        showLoginScreen();

        primaryStage.show();
    }

    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(LostFoundApp.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            loginScene = new Scene(root);
            loginScene.getStylesheets().add(LostFoundApp.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard(User user) {
        currentUser = user;
        try {
            String fxmlPath = getDashboardFXMLForRole(user.getRole());
            FXMLLoader loader = new FXMLLoader(LostFoundApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            dashboardScene = new Scene(root);
            dashboardScene.getStylesheets().add(LostFoundApp.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(dashboardScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDashboardFXMLForRole(Role role) {
        switch (role) {
            case ADMIN:
                return "/fxml/admin_dashboard.fxml";
            case SECURITY:
                return "/fxml/security_dashboard.fxml";
            default:
                return "/fxml/student_dashboard.fxml";
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void logout() {
        currentUser = null;
        showLoginScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
