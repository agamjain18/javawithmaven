package com.mycompany.mavenproject1;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Adjust these credentials to match your MySQL database setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/java?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Put your MySQL password here

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String usernameInput = request.getParameter("username");
        String passwordInput = request.getParameter("password");
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        boolean isValidUser = false;
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database and verify credentials
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                
                // --- Auto-Create Table for convenience ---
                String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(50) NOT NULL UNIQUE, "
                        + "password VARCHAR(255) NOT NULL)";
                try (PreparedStatement createStmt = conn.prepareStatement(createTableSQL)) {
                    createStmt.execute();
                }
                
                // Ensure at least one default user exists (admin / admin123)
                String insertDefaultSQL = "INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertDefaultSQL)) {
                    insertStmt.execute();
                }
                // -----------------------------------------

                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, usernameInput);
                    stmt.setString(2, passwordInput);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            isValidUser = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("error: " + e.getMessage());
            return;
        }

        if (isValidUser) {
            out.print("success");
        } else {
            out.print("fail");
        }
    }
}
