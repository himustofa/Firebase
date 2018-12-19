package com.apps.firebase.student;

public class Student {
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String studentPassword;
    private int studentAge;

    public Student() {
    }

    public Student(String studentEmail, String studentPassword) {
        this.studentEmail = studentEmail;
        this.studentPassword = studentPassword;
    }

    public Student(String studentId, String studentName, String studentEmail, String studentPassword, int studentAge) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPassword = studentPassword;
        this.studentAge = studentAge;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public int getStudentAge() {
        return studentAge;
    }

    public void setStudentAge(int studentAge) {
        this.studentAge = studentAge;
    }
}
