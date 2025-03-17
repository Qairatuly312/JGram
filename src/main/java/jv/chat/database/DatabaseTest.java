package jv.chat.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Соединение с базой данных установлено!");
            } else {
                System.out.println("Не удалось подключиться к базе данных.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
