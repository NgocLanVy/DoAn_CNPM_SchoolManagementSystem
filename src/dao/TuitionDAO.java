/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Tuition;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class TuitionDAO {
// Danh sách khoản học phí của 1 học sinh cụ thể, dùng cho màn hình "Xem học phí" (Student/Parent)

    public List<Object[]> getByStudent(int studentProfileId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.Id, t.Semester, t.Amount, t.DueDate, t.PaidDate, t.Status "
                + "FROM Tuitions t "
                + "WHERE t.StudentProfileId = ? "
                + "ORDER BY t.DueDate DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentProfileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("Id"), rs.getString("Semester"), rs.getBigDecimal("Amount"),
                        rs.getDate("DueDate"), rs.getDate("PaidDate"), rs.getString("Status")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByStudent Tuition: " + e.getMessage());
        }
        return list;
    }

    public List<Object[]> getAll() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.Id, sp.StudentCode, u.FullName, t.Semester, t.Amount, t.DueDate, t.PaidDate, t.Status "
                + "FROM Tuitions t "
                + "JOIN StudentProfiles sp ON t.StudentProfileId = sp.Id "
                + "JOIN Users u ON sp.UserId = u.Id "
                + "ORDER BY t.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("Id"), rs.getString("StudentCode"), rs.getString("FullName"),
                    rs.getString("Semester"), rs.getBigDecimal("Amount"), rs.getDate("DueDate"),
                    rs.getDate("PaidDate"), rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll Tuition: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(int studentProfileId, String semester, BigDecimal amount, Date dueDate) {
        String sql = "INSERT INTO Tuitions (StudentProfileId, Semester, Amount, DueDate, Status) VALUES (?,?,?,?,'Unpaid')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentProfileId);
            ps.setString(2, semester);
            ps.setBigDecimal(3, amount);
            ps.setDate(4, dueDate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Tuition: " + e.getMessage());
            return false;
        }
    }

    // Đánh dấu đã đóng học phí
    public boolean markAsPaid(int id) {
        String sql = "UPDATE Tuitions SET Status='Paid', PaidDate=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi markAsPaid Tuition: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Tuitions WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Tuition: " + e.getMessage());
            return false;
        }
    }
}
