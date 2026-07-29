/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class GradeDAO {

    public List<Grade> getByStudent(int studentProfileId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, sub.SubjectName FROM Grades g "
                + "JOIN Subjects sub ON g.SubjectId = sub.Id "
                + "WHERE g.StudentProfileId = ? ORDER BY g.Id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentProfileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setId(rs.getInt("Id"));
                    g.setStudentProfileId(rs.getInt("StudentProfileId"));
                    g.setSubjectId(rs.getInt("SubjectId"));
                    g.setExamType(rs.getString("ExamType"));
                    g.setScore(rs.getDouble("Score"));
                    g.setSemester(rs.getString("Semester"));
                    g.setSubjectName(rs.getString("SubjectName"));
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByStudent Grade: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Grade g) {
        String sql = "INSERT INTO Grades (StudentProfileId, SubjectId, ExamType, Score, Semester) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, g.getStudentProfileId());
            ps.setInt(2, g.getSubjectId());
            ps.setString(3, g.getExamType());
            ps.setDouble(4, g.getScore());
            ps.setString(5, g.getSemester());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Grade: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Grade g) {
        String sql = "UPDATE Grades SET ExamType=?, Score=?, Semester=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getExamType());
            ps.setDouble(2, g.getScore());
            ps.setString(3, g.getSemester());
            ps.setInt(4, g.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update Grade: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Grades WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Grade: " + e.getMessage());
            return false;
        }
    }
}
