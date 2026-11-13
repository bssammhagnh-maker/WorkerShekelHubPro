package com.example.workershekel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:workers_hub_pro.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS workers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "daily_wage REAL NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "worker_name TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "date TEXT NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS finances (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT NOT NULL, " +
                    "category TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "description TEXT, " +
                    "date TEXT NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS materials (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "material_name TEXT NOT NULL, " +
                    "quantity TEXT NOT NULL, " +
                    "cost REAL NOT NULL, " +
                    "supplier TEXT, " +
                    "date TEXT NOT NULL" +
                    ");");

        } catch (SQLException e) {
            System.err.println("خطأ في قاعدة البيانات: " + e.getMessage());
        }
    }

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static void addWorker(String name, double dailyWage) throws SQLException {
        String sql = "INSERT INTO workers (name, daily_wage) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, dailyWage);
            ps.executeUpdate();
        }
    }

    public static void deleteWorker(int id) throws SQLException {
        String sql = "DELETE FROM workers WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<Worker> getAllWorkers() throws SQLException {
        List<Worker> list = new ArrayList<>();
        String sql = "SELECT * FROM workers";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Worker(rs.getInt("id"), rs.getString("name"), rs.getDouble("daily_wage")));
            }
        }
        return list;
    }

    public static void addAttendance(String workerName, String status, LocalDate date) throws SQLException {
        String sql = "INSERT INTO attendance (worker_name, status, date) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workerName);
            ps.setString(2, status);
            ps.setString(3, date.toString());
            ps.executeUpdate();
        }
    }

    public static void deleteAttendance(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<AttendanceRecord> getAllAttendance() throws SQLException {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY date DESC";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AttendanceRecord(
                        rs.getInt("id"),
                        rs.getString("worker_name"),
                        rs.getString("status"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return list;
    }

    public static void addFinance(String type, String category, double amount, String description, LocalDate date) throws SQLException {
        String sql = "INSERT INTO finances (type, category, amount, description, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, category);
            ps.setDouble(3, amount);
            ps.setString(4, description);
            ps.setString(5, date.toString());
            ps.executeUpdate();
        }
    }

    public static void deleteFinance(int id) throws SQLException {
        String sql = "DELETE FROM finances WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<FinanceRecord> getAllFinances() throws SQLException {
        List<FinanceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM finances ORDER BY date DESC";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new FinanceRecord(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return list;
    }

    public static void addMaterial(String materialName, String quantity, double cost, String supplier, LocalDate date) throws SQLException {
        String sql = "INSERT INTO materials (material_name, quantity, cost, supplier, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, materialName);
            ps.setString(2, quantity);
            ps.setDouble(3, cost);
            ps.setString(4, supplier);
            ps.setString(5, date.toString());
            ps.executeUpdate();
        }
    }

    public static void deleteMaterial(int id) throws SQLException {
        String sql = "DELETE FROM materials WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static List<MaterialRecord> getAllMaterials() throws SQLException {
        List<MaterialRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM materials ORDER BY date DESC";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MaterialRecord(
                        rs.getInt("id"),
                        rs.getString("material_name"),
                        rs.getString("quantity"),
                        rs.getDouble("cost"),
                        rs.getString("supplier"),
                        LocalDate.parse(rs.getString("date"))
                ));
            }
        }
        return list;
    }

    public static void addMaterial(WorkerShift shift) {

    }
}