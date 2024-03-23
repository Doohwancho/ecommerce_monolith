package com.cho.ecommerce.smoke_test;

import com.cho.ecommerce.Application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("local")
@Tag("smoke") //to run, type "mvn test -Dgroups=smoke"
public class MySQLConnectionTest {
    
    @Test
    void healthCheck() {
        // JDBC URL parts: jdbc:mysql://[host1][:port1][,[host2][:port2]]...[/[database]]...
        String jdbcUrl = "jdbc:mysql://localhost:3306/ecommerce-database";
        String username = "root";
        String password = "";
        
        Connection conn = null;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            
            // If the connection was successful, print a message
            if (conn != null) {
                System.out.println("Successfully connected to MySQL database.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error connecting to the MySQL database.");
            e.printStackTrace();
        } finally {
            // Close the connection if it was opened
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
