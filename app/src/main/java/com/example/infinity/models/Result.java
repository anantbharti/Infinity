package com.example.infinity.models;

import java.io.Serializable;

public class Result implements Serializable {
    private String testCode;
    private String studentId;
    private String studentName;
    private int studentRoll;
    private String date;
    private String testName;
    private int score;
    private int minimizeCount;

    public Result() {
    }

    public Result(String testCode, String studentId, String studentName, int studentRoll, String date, String testName, int score, int minimizeCount) {
        this.testCode = testCode;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRoll = studentRoll;
        this.date = date;
        this.testName = testName;
        this.score = score;
        this.minimizeCount = minimizeCount;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
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

    public int getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(int studentRoll) {
        this.studentRoll = studentRoll;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMinimizeCount() {
        return minimizeCount;
    }

    public void setMinimizeCount(int minimizeCount) {
        this.minimizeCount = minimizeCount;
    }
}
