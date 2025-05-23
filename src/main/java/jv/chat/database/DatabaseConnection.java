package jv.chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/JGram_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "SONIT2CX";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found!", e);
        }
    }

//    public static void sendMessageDB(String username, String message){
//        String query = "INSERT INTO messages (username, message) VALUES (?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)){
//            stmt.setString(1, username);
//            stmt.setString(2, message);
//            stmt.executeUpdate();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
