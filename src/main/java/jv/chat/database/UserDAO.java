package jv.chat.database;

import jv.chat.models.User;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
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
        String query = "SELECT * FROM users WHERE username = ?";
        System.out.println("Querying database for user: " + username);
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

    public static Integer extractReceiverId(String message) {
        if (message.startsWith("@")) {
            String[] parts = message.split(" ", 2);
            if (parts.length < 2) {
                System.out.println("Ошибка: Некорректный формат сообщения: " + message);
                return null; // Сообщение невалидное
            }
            String receiverUsername = parts[0].substring(1).trim();
            Integer receiverId = getUserIdByUsername(receiverUsername);

            if (receiverId == null || receiverId <= 0) {
                System.out.println("Ошибка: Пользователь не найден: " + receiverUsername);
                return null; // Вернем null, если пользователя нет
            }

            System.out.println("Extracted receiver ID: " + receiverId + " for username: " + receiverUsername);
            return receiverId;
        }
        return null; // NULL для группового сообщения
    }






    public static int getUserIdByUsername(String username) {
        if (username.startsWith("@")) {
            username = username.substring(1); // Убираем @
        }

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
