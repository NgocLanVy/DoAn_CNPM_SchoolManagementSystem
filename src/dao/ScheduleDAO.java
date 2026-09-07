/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class ScheduleDAO {

    public static final String[] DAY_NAMES = {"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};

    public static String dayName(int dayOfWeek) {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            return "?";
        }
        return DAY_NAMES[dayOfWeek];
    }

    public List<Object[]> getAll() {

        List<Object[]> list = new ArrayList<>();

        String sql = "SELECT sc.Id, "
                + "cl.ClassName, "
                + "sub.SubjectName, "
                + "u.FullName AS TeacherName, "
                + "sc.DayOfWeek, "
                + "sc.StartTime, "
                + "sc.EndTime, "
                + "sc.Room "
                + "FROM Schedules sc "
                + "JOIN SchoolClasses cl ON sc.SchoolClassId = cl.Id "
                + "JOIN Subjects sub ON sc.SubjectId = sub.Id "
                + "LEFT JOIN TeacherProfiles tp ON sc.TeacherId = tp.Id "
                + "LEFT JOIN Users u ON tp.UserId = u.Id "
                + "ORDER BY sc.DayOfWeek, sc.StartTime";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Object[]{
                    rs.getInt("Id"),
                    rs.getString("ClassName"),
                    rs.getString("SubjectName"),
                    rs.getString("TeacherName"),
                    rs.getInt("DayOfWeek"),
                    rs.getTime("StartTime"),
                    rs.getTime("EndTime"),
                    rs.getString("Room")
                });
            }

        } catch (SQLException e) {
            System.out.println("Lỗi getAll Schedule: " + e.getMessage());
        }

        return list;
    }

    /**
     * Danh sách tiết học của 1 lớp, mỗi phần tử: {Id, DayOfWeek(int),
     * SubjectName, TeacherName, StartTime, EndTime, Room}
     */
    public List<Object[]> getByClass(int schoolClassId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sc.Id, sc.DayOfWeek, sub.SubjectName, u.FullName AS TeacherName, "
                + "       sc.StartTime, sc.EndTime, sc.Room "
                + "FROM Schedules sc "
                + "JOIN Subjects sub ON sc.SubjectId = sub.Id "
                + "LEFT JOIN TeacherProfiles tp ON sc.TeacherId = tp.Id "
                + "LEFT JOIN Users u ON tp.UserId = u.Id "
                + "WHERE sc.SchoolClassId = ? "
                + "ORDER BY sc.DayOfWeek, sc.StartTime";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, schoolClassId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("Id"), rs.getInt("DayOfWeek"), rs.getString("SubjectName"),
                        rs.getString("TeacherName"), rs.getTime("StartTime"),
                        rs.getTime("EndTime"), rs.getString("Room")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByClass Schedule: " + e.getMessage());
        }
        return list;
    }

    /**
     * Toàn bộ lịch dạy của 1 giáo viên trên các lớp khác nhau, dùng cho màn
     * hình "Xem thời khóa biểu" của Teacher. Mỗi phần tử: {Id, DayOfWeek(int),
     * SubjectName, ClassName, StartTime, EndTime, Room}
     */
    public List<Object[]> getByTeacher(int teacherProfileId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sc.Id, sc.DayOfWeek, sub.SubjectName, cl.ClassName, "
                + "       sc.StartTime, sc.EndTime, sc.Room "
                + "FROM Schedules sc "
                + "JOIN Subjects sub ON sc.SubjectId = sub.Id "
                + "JOIN SchoolClasses cl ON sc.SchoolClassId = cl.Id "
                + "WHERE sc.TeacherId = ? "
                + "ORDER BY sc.DayOfWeek, sc.StartTime";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherProfileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("Id"), rs.getInt("DayOfWeek"), rs.getString("SubjectName"),
                        rs.getString("ClassName"), rs.getTime("StartTime"),
                        rs.getTime("EndTime"), rs.getString("Room")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByTeacher Schedule: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Schedule s) {
        String sql = "INSERT INTO Schedules (SchoolClassId, SubjectId, TeacherId, DayOfWeek, StartTime, EndTime, Room) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getSchoolClassId());
            ps.setInt(2, s.getSubjectId());
            if (s.getTeacherId() != null) {
                ps.setInt(3, s.getTeacherId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, s.getDayOfWeek());
            ps.setTime(5, s.getStartTime());
            ps.setTime(6, s.getEndTime());
            ps.setString(7, s.getRoom());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Schedule: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Schedule s) {
        String sql = "UPDATE Schedules SET SubjectId=?, TeacherId=?, DayOfWeek=?, StartTime=?, EndTime=?, Room=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getSubjectId());
            if (s.getTeacherId() != null) {
                ps.setInt(2, s.getTeacherId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, s.getDayOfWeek());
            ps.setTime(4, s.getStartTime());
            ps.setTime(5, s.getEndTime());
            ps.setString(6, s.getRoom());
            ps.setInt(7, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update Schedule: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Schedules WHERE Id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Schedule: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getByTeacherUserId(int userId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT sc.Id, "
                + "sc.DayOfWeek, "
                + "sub.SubjectName, "
                + "cl.ClassName, "
                + "sc.StartTime, "
                + "sc.EndTime, "
                + "sc.Room "
                + "FROM Schedules sc "
                + "JOIN Subjects sub ON sc.SubjectId = sub.Id "
                + "JOIN SchoolClasses cl ON sc.SchoolClassId = cl.Id "
                + "JOIN TeacherProfiles tp ON sc.TeacherId = tp.Id "
                + "WHERE tp.UserId = ? "
                + "ORDER BY sc.DayOfWeek, sc.StartTime";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("Id"),
                        rs.getInt("DayOfWeek"),
                        rs.getString("SubjectName"),
                        rs.getString("ClassName"),
                        rs.getTime("StartTime"),
                        rs.getTime("EndTime"),
                        rs.getString("Room")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByTeacherUserId Schedule: "+ e.getMessage());
        }
        return list;
    }
}
