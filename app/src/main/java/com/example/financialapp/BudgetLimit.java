package com.example.financialapp;

public class BudgetLimit {

    private String category;
    private int amount;
    private String id;
    private int month;   // digunakan untuk lock limit hanya bulan ini

    public BudgetLimit() {
        // required empty
    }

    public BudgetLimit(String category, int amount, String id) {
        this.category = category;
        this.amount = amount;
        this.id = id;
        this.month = 0;
    }

    // GETTERS
    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public int getMonth() {
        return month;
    }

    // SETTERS
    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
