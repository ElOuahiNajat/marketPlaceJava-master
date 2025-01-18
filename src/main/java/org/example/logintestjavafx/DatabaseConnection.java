package org.example.logintestjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String databaseUrl = "jdbc:mysql://localhost:3306/marketplace?useSSL=false&serverTimezone=UTC";
    private final String dbUser = "root"; // Remplacez par votre utilisateur MySQL
    private final String dbPassword = ""; // Remplacez par votre mot de passe MySQL (vide par défaut pour XAMPP)

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(databaseUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage());
        }
    }
}
