package com.example.infinity.utilities;


import com.example.infinity.models.User;

import java.util.HashSet;

public class Statics {
    public static User CUR_USER;
    public static String USERS_COLLECTION="Users";  // reference authId
    public static String TESTS_COLLECTION="Tests";  // reference test code
    public static String STUDENT = "S";
    public static String TEACHER = "T";
    public static String QUESTIONS_COLLECTION = "Questions";  // reference test code + question code
    public static String QUESTION_COUNT = "QuesCount";
    public static String STUDENT_COUNT = "StuCount";
    public static String COUNTERS = "Counters";      // reference test code
    public static String TEST_CODE = "testCode";
    public static String QUES_CODE = "quesCode";
    public static String RESULT_COLLECTION="Results";  // reference test code + student authId
    public static String RESPONSE_COLLECTION="Responses";  // reference test code + student authId
    public static String ATTEMPTS_COLLECTION = "Attempts"; // reference test code + student authId
    public static HashSet<String> DELETED_TESTS = new HashSet<String>();
}
