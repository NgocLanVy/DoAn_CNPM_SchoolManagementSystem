/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Lenovo
 */
public class ParentStudent {
    private int id;
    private int parentUserId;
    private int studentProfileId;
    private String relationship; // Cha / Mẹ / Người giám hộ

    public ParentStudent() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getParentUserId() { return parentUserId; }
    public void setParentUserId(int parentUserId) { this.parentUserId = parentUserId; }
    public int getStudentProfileId() { return studentProfileId; }
    public void setStudentProfileId(int studentProfileId) { this.studentProfileId = studentProfileId; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
}