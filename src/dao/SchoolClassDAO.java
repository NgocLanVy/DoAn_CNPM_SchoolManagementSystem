/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.SchoolClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class SchoolClassDAO {

    public List<SchoolClass> getAll() {
        List<SchoolClass> list = new ArrayList<>();
        String sql = "SELECT sc.Id, sc.ClassName, sc.GradeLevel, sc.SchoolYear, sc.HomeroomTeacherId, "
                   + "       u.FullName AS TeacherName "
                   + "FROM SchoolClasses sc "
                   + "LEFT JOIN TeacherProfiles tp ON sc.HomeroomTeacherId = tp.Id "
                   + "LEFT JOIN Users u ON tp.UserId = u.Id "
                   + "ORDER BY sc.Id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SchoolClass c = new SchoolClass();
                c.setId(rs.getInt("Id"));
                c.setClassName(rs.getString("ClassName"));
                c.setGradeLevel(rs.getInt("GradeLevel"));
                c.setSchoolYear(rs.getString("SchoolYear"));
                int teacherId = rs.getInt("HomeroomTeacherId");
                c.setHomeroomTeacherId(rs.wasNull() ? null : teacherId);
                c.setHomeroomTeacherName(rs.getString("TeacherName"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll SchoolClass: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(SchoolClass c) {
        String sql = "INSERT INTO SchoolClasses (ClassName, GradeLevel, SchoolYear, HomeroomTeacherId) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setInt(2, c.getGradeLevel());
            ps.setString(3, c.getSchoolYear());
            if (c.getHomeroomTeacherId() != null) ps.setInt(4, c.getHomeroomTeacherId()); else ps.setNull(4, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert SchoolClass: " + e.getMessage());
            return false;
        }
    }

    public boolean update(SchoolClass c) {
        String sql = "UPDATE SchoolClasses SET ClassName=?, GradeLevel=?, SchoolYear=?, HomeroomTeacherId=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getClassName());
            ps.setInt(2, c.getGradeLevel());
            ps.setString(3, c.getSchoolYear());
            if (c.getHomeroomTeacherId() != null) ps.setInt(4, c.getHomeroomTeacherId()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update SchoolClass: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM SchoolClasses WHERE Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete SchoolClass: " + e.getMessage());
            return false;
        }
    }
}
