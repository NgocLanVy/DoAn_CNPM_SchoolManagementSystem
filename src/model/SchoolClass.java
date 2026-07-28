/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Lenovo
 */
public class SchoolClass {

    private int id;
    private String className;
    private int gradeLevel;
    private String schoolYear;
    private Integer homeroomTeacherId;
    private String homeroomTeacherName; // để hiển thị lên JTable

    public SchoolClass() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Integer getHomeroomTeacherId() {
        return homeroomTeacherId;
    }

    public void setHomeroomTeacherId(Integer homeroomTeacherId) {
        this.homeroomTeacherId = homeroomTeacherId;
    }

    public String getHomeroomTeacherName() {
        return homeroomTeacherName;
    }

    public void setHomeroomTeacherName(String homeroomTeacherName) {
        this.homeroomTeacherName = homeroomTeacherName;
    }

    @Override
    public String toString() {
        return className;
    }
}

