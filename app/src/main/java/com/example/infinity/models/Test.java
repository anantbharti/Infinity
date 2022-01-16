package com.example.infinity.models;

import java.io.Serializable;

public class Test implements Serializable {
    private String name;
    private String subject;
    private int duration; // in minutes
    private String preparedById; //
    private String date;  // test prepared date
    private String testCode;  // 8 digit number

    public Test() {
    }

    public Test(String name, String subject, int duration, String preparedById, String date, String testCode) {
        this.name = name;
        this.subject = subject;
        this.duration = duration;
        this.preparedById = preparedById;
        this.date = date;
        this.testCode = testCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPreparedById() {
        return preparedById;
    }

    public void setPreparedById(String preparedById) {
        this.preparedById = preparedById;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }
}
