package com.campus.lostfound.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() {
        HikariConfig config = new HikariConfig();
        // Default values for database connection
        config.setJdbcUrl(System.getProperty("db.url", "jdbc:mysql://localhost:3306/campus_lost_found?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"));
        config.setUsername(System.getProperty("db.username", "root"));
        config.setPassword(System.getProperty("db.password", "password"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        
        // Add driver explicitly to prevent issues in desktop bundle environments
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource = new HikariDataSource(config);
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
