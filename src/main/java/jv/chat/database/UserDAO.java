package jv.chat.database;

import jv.chat.models.Message;
import jv.chat.models.User;

import javax.print.DocFlavor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getAllUsernames(int id) {
        String query = "SELECT username FROM jgram.users WHERE id!=?"; // Select only necessary columns
        List<String> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(username); // Assuming User has a constructor (id, username)
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly in production
        }

        return users;
    }

    public static int extractReceiverId(Message message) {
        if(message.getReceiverId() == -1) {
            return -1;
        }
        return message.getReceiverId();
    }

    public static int getUserIdByUsername(String username) {
        String query = "SELECT id FROM jgram.users WHERE username = ?";

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
