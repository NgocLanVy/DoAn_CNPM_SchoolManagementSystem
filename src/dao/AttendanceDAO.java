/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class AttendanceDAO {
// Lịch sử điểm danh của 1 học sinh trên TẤT CẢ môn học, dùng cho màn hình "Xem điểm danh"

    public List<Object[]> getByStudent(int studentProfileId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT a.[Date], sub.SubjectName, a.Status, a.Note "
                + "FROM Attendances a "
                + "JOIN Subjects sub ON a.SubjectId = sub.Id "
                + "WHERE a.StudentProfileId = ? "
                + "ORDER BY a.[Date] DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentProfileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getDate("Date"), rs.getString("SubjectName"),
                        rs.getString("Status"), rs.getString("Note")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByStudent Attendance: " + e.getMessage());
        }
        return list;
    }

    // Lấy danh sách học sinh của 1 lớp kèm trạng thái điểm danh (nếu đã có) trong 1 ngày + môn học
    public List<Object[]> getAttendanceByClassDate(int schoolClassId, int subjectId, Date date) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sp.Id AS StudentProfileId, sp.StudentCode, u.FullName, "
                + "       a.Id AS AttendanceId, a.Status, a.Note "
                + "FROM StudentProfiles sp "
                + "JOIN Users u ON sp.UserId = u.Id "
                + "LEFT JOIN Attendances a ON a.StudentProfileId = sp.Id AND a.SubjectId = ? AND a.Date = ? "
                + "WHERE sp.SchoolClassId = ? ORDER BY sp.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setDate(2, date);
            ps.setInt(3, schoolClassId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("StudentProfileId"),
                        rs.getString("StudentCode"),
                        rs.getString("FullName"),
                        rs.getString("Status") == null ? "Present" : rs.getString("Status"),
                        rs.getString("Note")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAttendanceByClassDate: " + e.getMessage());
        }
        return list;
    }

    // Lưu điểm danh: nếu đã tồn tại bản ghi (StudentProfileId+SubjectId+Date) thì update, chưa có thì insert
    public boolean saveAttendance(int studentProfileId, int subjectId, Date date, String status, String note, Integer teacherUserId) {
        String checkSql = "SELECT Id FROM Attendances WHERE StudentProfileId=? AND SubjectId=? AND [Date]=?";
        String insertSql = "INSERT INTO Attendances (StudentProfileId, SubjectId, [Date], Status, Note, RecordedByTeacherId) VALUES (?,?,?,?,?,?)";
        String updateSql = "UPDATE Attendances SET Status=?, Note=? WHERE Id=?";

        try (Connection con = DBConnection.getConnection()) {
            Integer existingId = null;
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, studentProfileId);
                ps.setInt(2, subjectId);
                ps.setDate(3, date);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("Id");
                    }
                }
            }
            if (existingId != null) {
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setString(1, status);
                    ps.setString(2, note);
                    ps.setInt(3, existingId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, studentProfileId);
                    ps.setInt(2, subjectId);
                    ps.setDate(3, date);
                    ps.setString(4, status);
                    ps.setString(5, note);
                    if (teacherUserId != null) {
                        ps.setInt(6, teacherUserId);
                    } else {
                        ps.setNull(6, Types.INTEGER);
                    }
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi saveAttendance: " + e.getMessage());
            return false;
        }
    }
}
