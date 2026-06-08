package com.campus.lostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
    protected DatabaseConnection dbConnection;

    public BaseDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    protected abstract String getFindAllQuery();

    public List<T> findAll() throws SQLException {
        List<T> items = new ArrayList<>();
        String query = getFindAllQuery();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(mapResultSetToEntity(rs));
            }
        }
        return items;
    }

    public void executeInTransaction(TransactionalOperation operation) throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                operation.execute(conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @FunctionalInterface
    public interface TransactionalOperation {
        void execute(Connection conn) throws SQLException;
    }
}
