/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class SubjectDAO {

    public List<Subject> getAll() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM Subjects ORDER BY Id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Subject s = new Subject();
                s.setId(rs.getInt("Id"));
                s.setSubjectName(rs.getString("SubjectName"));
                s.setSubjectCode(rs.getString("SubjectCode"));
                s.setDescription(rs.getString("Description"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getAll Subject: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Subject s) {
        String sql = "INSERT INTO Subjects (SubjectName, SubjectCode, Description) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectName());
            ps.setString(2, s.getSubjectCode());
            ps.setString(3, s.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Subject: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Subject s) {
        String sql = "UPDATE Subjects SET SubjectName=?, SubjectCode=?, Description=? WHERE Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectName());
            ps.setString(2, s.getSubjectCode());
            ps.setString(3, s.getDescription());
            ps.setInt(4, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi update Subject: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Subjects WHERE Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi delete Subject: " + e.getMessage());
            return false;
        }
    }
}

