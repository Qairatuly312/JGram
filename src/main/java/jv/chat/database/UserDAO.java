package jv.chat.database;

import jv.chat.models.User;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO jgram.users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.getMessage();
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM jgram.users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password")/*,rs.getBoolean("isOnline")*/);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int extractReceiverId(String message) {
        if (message.startsWith("@")) {
            String receiverUsername = message.split(" ")[0].substring(1); // Remove '@'
            return UserDAO.getUserIdByUsername(receiverUsername);
        }
        return -1; // No receiver specified
    }


    public static int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // User not found
    }
}
