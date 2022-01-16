package com.example.infinity.models;

import java.io.Serializable;

public class Question implements Serializable {
    private int quesNo;
    private String quesCode;
    private String statement;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String imageURL;
    private String testCode;

    public Question() {
    }

    public Question(int quesNo, String statement, String optionA, String optionB, String optionC, String optionD, String correctOption,String imageURL,String quesCode,String testCode) {
        this.quesNo = quesNo;
        this.statement = statement;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.imageURL = imageURL;
        this.quesCode = quesCode;
        this.testCode = testCode;
    }

    public int getQuesNo() {
        return quesNo;
    }

    public void setQuesNo(int quesNo) {
        this.quesNo = quesNo;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getQuesCode() {
        return quesCode;
    }

    public void setQuesCode(String quesCode) {
        this.quesCode = quesCode;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }
}
