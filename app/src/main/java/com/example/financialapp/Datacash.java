package com.example.financialapp;

public class Datacash {
    String categoryData, type, id, date;
    int amount, month;

    public Datacash() {
    }

    public Datacash(String categoryData, String type, String id, String date, int amount, int month) {
        this.categoryData = categoryData;
        this.type = type;
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.month = month;
    }

    public String getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(String categoryData) {
        this.categoryData = categoryData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
