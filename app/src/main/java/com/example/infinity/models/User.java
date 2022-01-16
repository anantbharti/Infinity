package com.example.infinity.models;

public class User {
    private String name;
    private String email;
    private String school;
    private String authId;
    private int rollNo;
    private String des; // class/standard for students, subject for teachers
    private String userType; //Student or Teacher

    public User() {
    }

    public User(String name, String email, String school, String authId, String des, String userType,int rollNo) {
        this.name = name;
        this.email = email;
        this.school = school;
        this.authId = authId;
        this.des = des;
        this.userType = userType;
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }
}
