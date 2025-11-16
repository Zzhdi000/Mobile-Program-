package com.example.financialapp;

public class Feedback {
    public String id;
    public String userId;
    public String message;
    public String date;

    public Feedback() {}

    public Feedback(String id, String userId, String message, String date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
    }
}

