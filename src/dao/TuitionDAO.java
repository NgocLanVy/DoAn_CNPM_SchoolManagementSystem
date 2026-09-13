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

        String sql = "SELECT Id, TuitionType, Semester, Amount, DueDate, PaidDate, Status, PaymentMethod "
                + "FROM Tuitions "
                + "WHERE StudentProfileId = ? "
                + "ORDER BY Id DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentProfileId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new Object[]{
                        rs.getInt("Id"),
                        rs.getString("TuitionType"),
                        rs.getString("Semester"),
                        rs.getBigDecimal("Amount"),
                        rs.getDate("DueDate"),
                        rs.getDate("PaidDate"),
                        rs.getString("Status"),
                        rs.getString("PaymentMethod")
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

    public boolean insert(int studentProfileId,String tuitionType,String semester,BigDecimal amount,Date dueDate) {
        String sql = "INSERT INTO Tuitions "
                + "(StudentProfileId, TuitionType, Semester, Amount, DueDate, Status) "
                + "VALUES (?, ?, ?, ?, ?, 'Unpaid')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentProfileId);
            ps.setString(2, tuitionType);
            ps.setString(3, semester);
            ps.setBigDecimal(4, amount);
            ps.setDate(5, dueDate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đánh dấu đã đóng học phí + lưu phương thức thanh toán
    public boolean markAsPaid(int id, String paymentMethod) {
        String sql = "UPDATE Tuitions "
                + "SET Status='Paid', PaidDate=?, PaymentMethod=? "
                + "WHERE Id=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(2, paymentMethod);
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi markAsPaid Tuition: " + e.getMessage());
            return false;
        }
    }

    // Dùng cho màn hình quản lý học phí của Admin
    public boolean markAsPaid(int id) {
        return markAsPaid(id, "Admin xác nhận");
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
