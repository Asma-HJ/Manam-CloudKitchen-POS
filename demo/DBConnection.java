package com.cloudkitchen.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/manam_cloud_kitchen?useSSL=false&serverTimezone=UTC";
            return DriverManager.getConnection(url, "root", "Asma@08");
        } catch (Exception e) {
            System.out.println("❌ Database Connection Failed: " + e.getMessage());
            return null;
        }
    }
}
