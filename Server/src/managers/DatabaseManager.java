package managers;

import models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/studs";
    private final String user = "s434931";
    private final String password = "250806";
    private Connection connection;
    private final Map<Long, HumanBeing> collection;

    public DatabaseManager() {
        this.collection = new ConcurrentHashMap<>();
        connect();
        loadHumanBeings();
    }

    private void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных установлено: " + url);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
        }
    }

    public Map<Long, HumanBeing> getCollection() {
        return collection;
    }

    public List<HumanBeing> loadHumanBeings() {
        collection.clear();
        List<HumanBeing> humans = new ArrayList<>();
        String query = "SELECT * FROM human_beings";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HumanBeing human = new HumanBeing();
                human.setId(rs.getLong("id"));
                human.setName(rs.getString("name"));
                human.setCoordinates(new Coordinates(
                        rs.getDouble("coordinate_x"),
                        rs.getFloat("coordinate_y")
                ));
                human.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                human.setRealHero(rs.getBoolean("real_hero"));
                human.setHasToothpick(rs.getObject("has_toothpick") != null ? rs.getBoolean("has_toothpick") : null);
                human.setImpactSpeed(rs.getLong("impact_speed"));
                human.setWeaponType(WeaponType.valueOf(rs.getString("weapon_type")));
                human.setMood(rs.getString("mood"));
                human.setCar(new Car(rs.getString("car_name")));
                human.setUserId(rs.getInt("user_id"));
                collection.put(human.getId(), human);
                humans.add(human);
            }
            System.out.println("Коллекция загружена из базы данных: " + collection.size() + " элементов");
            return humans;
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки коллекции: " + e.getMessage());
            return humans;
        }
    }

    public boolean add(HumanBeing humanBeing, Integer userId) {
        String sql = "INSERT INTO human_beings (name, coordinate_x, coordinate_y, creation_date, impact_speed, real_hero, has_toothpick, weapon_type, mood, car_name, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            System.out.println("Debug - Adding human being:");
            System.out.println("Name: " + humanBeing.getName());
            System.out.println("Coordinates: x=" + humanBeing.getCoordinates().getX() + ", y=" + humanBeing.getCoordinates().getY());
            System.out.println("Creation date: " + humanBeing.getCreationDate());
            System.out.println("Impact speed: " + humanBeing.getImpactSpeed());
            System.out.println("Real hero: " + humanBeing.getRealHero());
            System.out.println("Has toothpick: " + humanBeing.getHasToothpick());
            System.out.println("Weapon type: " + humanBeing.getWeaponType());
            System.out.println("Mood: " + humanBeing.getMood());
            System.out.println("Car name: " + humanBeing.getCar().getName());
            System.out.println("User ID: " + userId);

            pstmt.setString(1, humanBeing.getName());
            pstmt.setDouble(2, humanBeing.getCoordinates().getX());
            pstmt.setFloat(3, humanBeing.getCoordinates().getY());
            pstmt.setTimestamp(4, Timestamp.valueOf(humanBeing.getCreationDate()));
            pstmt.setLong(5, humanBeing.getImpactSpeed());
            pstmt.setBoolean(6, humanBeing.getRealHero());
            if (humanBeing.getHasToothpick() != null) {
                pstmt.setBoolean(7, humanBeing.getHasToothpick());
            } else {
                pstmt.setNull(7, Types.BOOLEAN);
            }
            pstmt.setString(8, humanBeing.getWeaponType().toString());
            pstmt.setString(9, humanBeing.getMood().toString());
            pstmt.setString(10, humanBeing.getCar().getName());
            pstmt.setInt(11, userId);

            System.out.println("Debug - Executing SQL: " + sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                humanBeing.setId(id);
                humanBeing.setUserId(userId);
                collection.put(id, humanBeing);
                System.out.println("Debug - Successfully added with ID: " + id);
                return true;
            } else {
                System.err.println("Debug - No ID returned from insert");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Debug - SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeHumanBeing(Long id, Integer userId) {
        if (id == null || userId == null) return false;
        String query = "DELETE FROM human_beings WHERE id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                collection.remove(id);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка удаления: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHumanBeing(Long id, HumanBeing humanBeing, Integer userId) {
        if (id == null || humanBeing == null || userId == null) return false;
        String query = "UPDATE human_beings SET name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?, impact_speed = ?, real_hero = ?, has_toothpick = ?, weapon_type = ?, mood = ?, car_name = ? WHERE id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, humanBeing.getName());
            pstmt.setDouble(2, humanBeing.getCoordinates().getX());
            pstmt.setFloat(3, humanBeing.getCoordinates().getY());
            pstmt.setTimestamp(4, Timestamp.valueOf(humanBeing.getCreationDate()));
            pstmt.setLong(5, humanBeing.getImpactSpeed());
            pstmt.setBoolean(6, humanBeing.getRealHero());
            if (humanBeing.getHasToothpick() != null) {
                pstmt.setBoolean(7, humanBeing.getHasToothpick());
            } else {
                pstmt.setNull(7, Types.BOOLEAN);
            }
            pstmt.setString(8, humanBeing.getWeaponType().toString());
            pstmt.setString(9, humanBeing.getMood().toString());
            pstmt.setString(10, humanBeing.getCar().getName());
            pstmt.setLong(11, id);
            pstmt.setInt(12, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                humanBeing.setId(id);
                humanBeing.setUserId(userId);
                collection.put(id, humanBeing);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления: " + e.getMessage());
            return false;
        }
    }

    public boolean clearHumanBeings(Integer userId) {
        if (userId == null) return false;
        String query = "DELETE FROM human_beings WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            collection.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
            return rowsAffected > 0;
        }
        catch (SQLException e) {
            System.err.println("Ошибка очистки: " + e.getMessage());
            return false;
        }
    }

    public Integer loginUser(String username, String password) {
        String query = "SELECT id, password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String hashedInputPassword = hashPassword(password);
                if (hashedInputPassword.equals(storedPassword)) {
                    return rs.getInt("id");
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка авторизации: " + e.getMessage());
            return null;
        }
    }

    public Integer registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String hashedPassword = hashPassword(password);
            System.out.println("Debug - Registration:");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Hashed password: " + hashedPassword);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return null;
        }
    }

    private String hashPassword(String password) {
        // Используем более сложный способ хеширования
        int hash = password.hashCode();
        // Добавляем соль и дополнительное хеширование
        hash = hash * 31 + "SALT".hashCode();
        hash = hash * 31 + password.length();
        return String.format("%d", hash);
    }

    public void saveCollection() {
        String deleteQuery = "DELETE FROM human_beings";
        String insertQuery = "INSERT INTO human_beings (id, name, coordinate_x, coordinate_y, creation_date, impact_speed, real_hero, has_toothpick, weapon_type, mood, car_name, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            // Clear the table
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(deleteQuery);
            }

            // Insert all HumanBeings
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                for (HumanBeing human : collection.values()) {
                    pstmt.setLong(1, human.getId());
                    pstmt.setString(2, human.getName());
                    pstmt.setDouble(3, human.getCoordinates().getX());
                    pstmt.setFloat(4, human.getCoordinates().getY());
                    pstmt.setTimestamp(5, Timestamp.valueOf(human.getCreationDate()));
                    pstmt.setLong(6, human.getImpactSpeed());
                    pstmt.setBoolean(7, human.getRealHero());
                    if (human.getHasToothpick() != null) {
                        pstmt.setBoolean(8, human.getHasToothpick());
                    } else {
                        pstmt.setNull(8, Types.BOOLEAN);
                    }
                    pstmt.setString(9, human.getWeaponType().toString());
                    pstmt.setString(10, human.getMood().toString());
                    pstmt.setString(11, human.getCar().getName());
                    pstmt.setInt(12, human.getUserId());
                    pstmt.executeUpdate();
                }
            }
            System.out.println("Коллекция сохранена в базу данных: " + collection.size() + " элементов");
        } catch (SQLException e) {
            System.err.println("Ошибка сохранения коллекции: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAllUsers() {
        String query = "SELECT username FROM users ORDER BY username";
        StringBuilder result = new StringBuilder();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.append(rs.getString("username")).append("\n");
            }
            return result.toString();
        } catch (SQLException e) {
            System.err.println("Ошибка получения списка пользователей: " + e.getMessage());
            return "";
        }
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка удаления пользователя: " + e.getMessage());
            return false;
        }
    }
}