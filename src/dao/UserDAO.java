/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class UserDAO {

    // Đăng nhập: kiểm tra username + password, trả về User nếu đúng, null nếu sai
    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE Username = ? AND PasswordHash = ? AND IsActive = 1";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // Thực tế nên hash (BCrypt) trước khi so sánh
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi login: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll Users: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(User u) {
        String sql = "INSERT INTO Users (Username, PasswordHash, FullName, Address, Gender, Email, Phone, IsActive, RoleId) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getAddress());
            ps.setString(5, u.getGender());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getPhone());
            ps.setBoolean(8, u.isActive());
            ps.setInt(9, u.getRoleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert User: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User u) {
        String sql = "UPDATE Users SET FullName=?, Address=?, Gender=?, Email=?, Phone=?, IsActive=?, RoleId=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getAddress());
            ps.setString(3, u.getGender());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            ps.setBoolean(6, u.isActive());
            ps.setInt(7, u.getRoleId());
            ps.setInt(8, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update User: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Users WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete User: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("Id"));
        u.setUsername(rs.getString("Username"));
        u.setPasswordHash(rs.getString("PasswordHash"));
        u.setFullName(rs.getString("FullName"));
        u.setAddress(rs.getString("Address"));
        u.setGender(rs.getString("Gender"));
        u.setEmail(rs.getString("Email"));
        u.setPhone(rs.getString("Phone"));
        u.setActive(rs.getBoolean("IsActive"));
        u.setRoleId(rs.getInt("RoleId"));
        return u;
    }

    public boolean updateProfile(User user) {

        String sql = "UPDATE Users "
                + "SET FullName = ?, "
                + "Address = ?, "
                + "Gender = ?, "
                + "Email = ?, "
                + "Phone = ? "
                + "WHERE Id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAddress());
            ps.setString(3, user.getGender());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi update profile: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPassword) {

        String sql = "UPDATE Users SET PasswordHash = ? WHERE Id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }
}
