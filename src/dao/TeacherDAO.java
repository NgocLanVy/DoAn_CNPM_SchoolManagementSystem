/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.TeacherProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class TeacherDAO {
// Lấy hồ sơ giáo viên theo UserId (dùng khi Teacher đăng nhập, cần biết TeacherProfileId của mình)

    public TeacherProfile getByUserId(int userId) {
        String sql = "SELECT tp.Id, tp.UserId, tp.TeacherCode, tp.Specialization, tp.HireDate, u.FullName "
                + "FROM TeacherProfiles tp JOIN Users u ON tp.UserId = u.Id WHERE tp.UserId = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TeacherProfile t = new TeacherProfile();
                    t.setId(rs.getInt("Id"));
                    t.setUserId(rs.getInt("UserId"));
                    t.setTeacherCode(rs.getString("TeacherCode"));
                    t.setSpecialization(rs.getString("Specialization"));
                    t.setHireDate(rs.getDate("HireDate"));
                    t.setFullName(rs.getString("FullName"));
                    return t;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByUserId Teacher: " + e.getMessage());
        }
        return null;
    }

    public List<TeacherProfile> getAll() {
        List<TeacherProfile> list = new ArrayList<>();
        String sql = "SELECT tp.Id, tp.UserId, tp.TeacherCode, tp.Specialization, tp.HireDate, u.FullName "
                + "FROM TeacherProfiles tp JOIN Users u ON tp.UserId = u.Id ORDER BY tp.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TeacherProfile t = new TeacherProfile();
                t.setId(rs.getInt("Id"));
                t.setUserId(rs.getInt("UserId"));
                t.setTeacherCode(rs.getString("TeacherCode"));
                t.setSpecialization(rs.getString("Specialization"));
                t.setHireDate(rs.getDate("HireDate"));
                t.setFullName(rs.getString("FullName"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll Teacher: " + e.getMessage());
        }
        return list;
    }

    // Thêm giáo viên: tạo Users (RoleId = 2) trước, sau đó tạo TeacherProfiles
    public boolean insert(String username, String password, String fullName, String gender,
            String teacherCode, String specialization, Date hireDate) {
        String sqlUser = "INSERT INTO Users (Username, PasswordHash, FullName, Gender, IsActive, RoleId) "
                + "VALUES (?, ?, ?, ?, 1, 2)"; // RoleId 2 = Teacher
        String sqlProfile = "INSERT INTO TeacherProfiles (UserId, TeacherCode, Specialization, HireDate) VALUES (?,?,?,?)";

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
                ps2.setString(2, teacherCode);
                ps2.setString(3, specialization);
                if (hireDate != null) {
                    ps2.setDate(4, hireDate);
                } else {
                    ps2.setNull(4, Types.DATE);
                }
                ps2.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Teacher: " + e.getMessage());
            return false;
        }
    }

    public boolean update(TeacherProfile t,String fullName,String gender) {
        String sqlUser= "UPDATE Users "
                + "SET FullName = ?, Gender = ? "
                + "WHERE Id = ?";
        String sqlProfile= "UPDATE TeacherProfiles "
                + "SET TeacherCode = ?, "
                + "Specialization = ?, "
                + "HireDate = ? "
                + "WHERE Id = ?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                // UPDATE USERS
                try (PreparedStatement ps= con.prepareStatement(sqlUser)) {
                    ps.setString(1, fullName);
                    ps.setString(2, gender);
                    ps.setInt(3, t.getUserId());
                    ps.executeUpdate();
                }

                // UPDATE TEACHER PROFILE
                try (PreparedStatement ps= con.prepareStatement(sqlProfile)) {
                    ps.setString(1, t.getTeacherCode());
                    ps.setString(2, t.getSpecialization());
                    if (t.getHireDate() != null) {
                        ps.setDate(3,new java.sql.Date(t.getHireDate().getTime()));
                    } else {
                        ps.setNull(3, Types.DATE);
                    }
                    ps.setInt(4, t.getId());
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Lỗi update Teacher: "+ e.getMessage());
               return false;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối update Teacher: "+ e.getMessage());
            return false;
        }
    }

    public boolean delete(int teacherProfileId, int userId) {
        String sqlDelProfile = "DELETE FROM TeacherProfiles WHERE Id=?";
        String sqlDelUser = "DELETE FROM Users WHERE Id=?";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlDelProfile)) {
                ps.setInt(1, teacherProfileId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(sqlDelUser)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Teacher: " + e.getMessage());
            return false;
        }
    }
}
