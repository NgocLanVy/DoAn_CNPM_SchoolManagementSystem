/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.TeachingAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class TeachingAssignmentDAO {

    public List<TeachingAssignment> getAll() {

        List<TeachingAssignment> list = new ArrayList<>();

        String sql
                = "SELECT ta.Id, "
                + "ta.SchoolClassId, "
                + "ta.SubjectId, "
                + "ta.TeacherProfileId, "
                + "ta.SchoolYear, "
                + "sc.ClassName, "
                + "s.SubjectName, "
                + "s.SubjectCode, "
                + "u.FullName AS TeacherName "
                + "FROM TeachingAssignments ta "
                + "JOIN SchoolClasses sc "
                + "ON ta.SchoolClassId = sc.Id "
                + "JOIN Subjects s "
                + "ON ta.SubjectId = s.Id "
                + "JOIN TeacherProfiles tp "
                + "ON ta.TeacherProfileId = tp.Id "
                + "JOIN Users u "
                + "ON tp.UserId = u.Id "
                + "ORDER BY ta.Id";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                TeachingAssignment ta = new TeachingAssignment();

                ta.setId(rs.getInt("Id"));
                ta.setSchoolClassId(rs.getInt("SchoolClassId"));
                ta.setSubjectId(rs.getInt("SubjectId"));
                ta.setTeacherProfileId(rs.getInt("TeacherProfileId"));
                ta.setSchoolYear(rs.getString("SchoolYear"));

                ta.setClassName(rs.getString("ClassName"));
                ta.setSubjectName(rs.getString("SubjectName"));
                ta.setSubjectCode(rs.getString("SubjectCode"));
                ta.setTeacherName(rs.getString("TeacherName"));

                list.add(ta);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Lỗi getAll TeachingAssignment: "
                    + e.getMessage()
            );
        }

        return list;
    }

    public boolean insert(TeachingAssignment ta) {

        String sql
                = "INSERT INTO TeachingAssignments "
                + "(SchoolClassId, SubjectId, TeacherProfileId, SchoolYear) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ta.getSchoolClassId());
            ps.setInt(2, ta.getSubjectId());
            ps.setInt(3, ta.getTeacherProfileId());
            ps.setString(4, ta.getSchoolYear());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Lỗi insert TeachingAssignment: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean update(TeachingAssignment ta) {

        String sql
                = "UPDATE TeachingAssignments "
                + "SET SchoolClassId=?, "
                + "SubjectId=?, "
                + "TeacherProfileId=?, "
                + "SchoolYear=? "
                + "WHERE Id=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ta.getSchoolClassId());
            ps.setInt(2, ta.getSubjectId());
            ps.setInt(3, ta.getTeacherProfileId());
            ps.setString(4, ta.getSchoolYear());
            ps.setInt(5, ta.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Lỗi update TeachingAssignment: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean delete(int id) {

        String sql
                = "DELETE FROM TeachingAssignments WHERE Id=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Lỗi delete TeachingAssignment: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public List<TeachingAssignment> getByTeacherUserId(int userId) {

        List<TeachingAssignment> list = new ArrayList<>();

        String sql
                = "SELECT ta.Id, "
                + "ta.SchoolClassId, "
                + "ta.SubjectId, "
                + "ta.TeacherProfileId, "
                + "ta.SchoolYear, "
                + "sc.ClassName, "
                + "s.SubjectName, "
                + "s.SubjectCode, "
                + "u.FullName AS TeacherName "
                + "FROM TeachingAssignments ta "
                + "JOIN SchoolClasses sc "
                + "ON ta.SchoolClassId = sc.Id "
                + "JOIN Subjects s "
                + "ON ta.SubjectId = s.Id "
                + "JOIN TeacherProfiles tp "
                + "ON ta.TeacherProfileId = tp.Id "
                + "JOIN Users u "
                + "ON tp.UserId = u.Id "
                + "WHERE tp.UserId = ? "
                + "ORDER BY ta.Id";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    TeachingAssignment ta = new TeachingAssignment();

                    ta.setId(rs.getInt("Id"));
                    ta.setSchoolClassId(rs.getInt("SchoolClassId"));
                    ta.setSubjectId(rs.getInt("SubjectId"));
                    ta.setTeacherProfileId(rs.getInt("TeacherProfileId"));
                    ta.setSchoolYear(rs.getString("SchoolYear"));

                    ta.setClassName(rs.getString("ClassName"));
                    ta.setSubjectName(rs.getString("SubjectName"));
                    ta.setSubjectCode(rs.getString("SubjectCode"));
                    ta.setTeacherName(rs.getString("TeacherName"));

                    list.add(ta);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Lỗi getByTeacherUserId: " + e.getMessage()
            );
        }

        return list;
    }
}
