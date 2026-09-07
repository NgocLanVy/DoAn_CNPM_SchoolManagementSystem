/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBConnection;
import model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class NotificationDAO {

    public boolean insert(int senderId, int receiverId, String title, String content) {
        String sql = "INSERT INTO Notifications (SenderId, ReceiverId, Title, Content, CreatedDate, IsRead) "
                + "VALUES (?,?,?,?,GETDATE(),0)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, title);
            ps.setString(4, content);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi insert Notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Danh sách thông báo NHẬN được - dùng cho Student/Parent.
     */
    public List<Notification> getByReceiver(int receiverId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.FullName AS SenderName FROM Notifications n "
                + "LEFT JOIN Users u ON n.SenderId = u.Id "
                + "WHERE n.ReceiverId = ? ORDER BY n.CreatedDate DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getByReceiver Notification: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lịch sử thông báo ĐÃ GỬI - dùng cho Admin/Teacher.
     */
    public List<Notification> getBySender(int senderId) {
        List<Notification> list = new ArrayList<>();
        String sql= "SELECT n.*, " + "u1.FullName AS SenderName, " + "u2.FullName AS ReceiverName "
                + "FROM Notifications n "
                + "LEFT JOIN Users u1 ON n.SenderId = u1.Id "
                + "LEFT JOIN Users u2 ON n.ReceiverId = u2.Id "
                + "WHERE n.SenderId = ? "
                + "ORDER BY n.CreatedDate DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = mapRow(rs);
                    n.setReceiverName(rs.getString("ReceiverName"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getBySender Notification: "+ e.getMessage());
        }
        return list;
    }

    public boolean markAsRead(int id) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE Id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi markAsRead Notification: " + e.getMessage());
            return false;
        }
    }

    public int countUnread(int receiverId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE ReceiverId = ? AND IsRead = 0";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi countUnread Notification: " + e.getMessage());
        }
        return 0;
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("Id"));
        n.setSenderId(rs.getInt("SenderId"));
        n.setReceiverId(rs.getInt("ReceiverId"));
        n.setTitle(rs.getString("Title"));
        n.setContent(rs.getString("Content"));
        n.setCreatedDate(rs.getTimestamp("CreatedDate"));
        n.setRead(rs.getBoolean("IsRead"));
        n.setSenderName(rs.getString("SenderName"));
        return n;
    }
}
