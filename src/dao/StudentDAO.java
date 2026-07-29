/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.StudentProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class StudentDAO {

    public List<StudentProfile> getAll() {
        List<StudentProfile> list = new ArrayList<>();
        String sql = "SELECT sp.Id, sp.UserId, sp.StudentCode, sp.SchoolClassId, "
                + "       u.FullName, u.Gender, sc.ClassName "
                + "FROM StudentProfiles sp "
                + "JOIN Users u ON sp.UserId = u.Id "
                + "LEFT JOIN SchoolClasses sc ON sp.SchoolClassId = sc.Id "
                + "ORDER BY sp.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StudentProfile sp = new StudentProfile();
                sp.setId(rs.getInt("Id"));
                sp.setUserId(rs.getInt("UserId"));
                sp.setStudentCode(rs.getString("StudentCode"));
                int classId = rs.getInt("SchoolClassId");
                sp.setSchoolClassId(rs.wasNull() ? null : classId);
                sp.setFullName(rs.getString("FullName"));
                sp.setGender(rs.getString("Gender"));
                sp.setClassName(rs.getString("ClassName"));
                list.add(sp);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll Student: " + e.getMessage());
        }
        return list;
    }

    public List<StudentProfile> search(String keyword) {
        List<StudentProfile> list = new ArrayList<>();
        String sql = "SELECT sp.Id, sp.UserId, sp.StudentCode, sp.SchoolClassId, "
                + "       u.FullName, u.Gender, sc.ClassName "
                + "FROM StudentProfiles sp "
                + "JOIN Users u ON sp.UserId = u.Id "
                + "LEFT JOIN SchoolClasses sc ON sp.SchoolClassId = sc.Id "
                + "WHERE u.FullName LIKE ? OR sp.StudentCode LIKE ? "
                + "ORDER BY sp.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentProfile sp = new StudentProfile();
                    sp.setId(rs.getInt("Id"));
                    sp.setUserId(rs.getInt("UserId"));
                    sp.setStudentCode(rs.getString("StudentCode"));
                    sp.setFullName(rs.getString("FullName"));
                    sp.setGender(rs.getString("Gender"));
                    sp.setClassName(rs.getString("ClassName"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi search Student: " + e.getMessage());
        }
        return list;
    }

    // Thêm học sinh: cần tạo Users trước, sau đó tạo StudentProfiles gắn UserId vừa tạo
    public boolean insert(String username, String password, String fullName, String gender,
            String studentCode, Integer schoolClassId) {
        String sqlUser = "INSERT INTO Users (Username, PasswordHash, FullName, Gender, IsActive, RoleId) "
                + "VALUES (?, ?, ?, ?, 1, 3)"; // RoleId 3 = Student
        String sqlProfile = "INSERT INTO StudentProfiles (UserId, StudentCode, SchoolClassId) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            int newUserId;
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, fullName);
                ps.setString(4, gender);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    newUserId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps2 = con.prepareStatement(sqlProfile)) {
                ps2.setInt(1, newUserId);
                ps2.setString(2, studentCode);
                if (schoolClassId != null) {
                    ps2.setInt(3, schoolClassId);
                } else {
                    ps2.setNull(3, Types.INTEGER);
                }
                ps2.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Student: " + e.getMessage());
            return false;
        }
    }

    public boolean update(StudentProfile sp) {
        String sql = "UPDATE StudentProfiles SET StudentCode=?, SchoolClassId=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getStudentCode());
            if (sp.getSchoolClassId() != null) {
                ps.setInt(2, sp.getSchoolClassId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, sp.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update Student: " + e.getMessage());
            return false;
        }
    }

    // Xóa học sinh: xóa StudentProfiles trước, sau đó có thể xóa luôn Users tương ứng
    public boolean delete(int studentProfileId, int userId) {
        String sqlDelProfile = "DELETE FROM StudentProfiles WHERE Id=?";
        String sqlDelUser = "DELETE FROM Users WHERE Id=?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlDelProfile)) {
                ps.setInt(1, studentProfileId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(sqlDelUser)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Student: " + e.getMessage());
            return false;
        }
    }
}
