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
public class ParentStudentDAO {

    public List<StudentProfile> getChildrenByParentUserId(int parentUserId) {
        List<StudentProfile> list = new ArrayList<>();
        String sql = "SELECT sp.Id, sp.UserId, sp.StudentCode, sp.SchoolClassId, "
                   + "       u.FullName, u.Gender, sc.ClassName "
                   + "FROM ParentStudents ps "
                   + "JOIN StudentProfiles sp ON ps.StudentProfileId = sp.Id "
                   + "JOIN Users u ON sp.UserId = u.Id "
                   + "LEFT JOIN SchoolClasses sc ON sp.SchoolClassId = sc.Id "
                   + "WHERE ps.ParentUserId = ? "
                   + "ORDER BY u.FullName";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parentUserId);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getChildrenByParentUserId: " + e.getMessage());
        }
        return list;
    }

    /** Thêm liên kết Phụ huynh - Học sinh (dùng khi Admin tạo hồ sơ phụ huynh, hiện chưa có Form riêng). */
    public boolean insert(int parentUserId, int studentProfileId, String relationship) {
        String sql = "INSERT INTO ParentStudents (ParentUserId, StudentProfileId, Relationship) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parentUserId);
            ps.setInt(2, studentProfileId);
            ps.setString(3, relationship);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert ParentStudent: " + e.getMessage());
            return false;
        }
    }
}
