package com.example.financialapp;

public class BudgetLimit {

    String category, id;
    int amount;

    public BudgetLimit(){

    }

    public BudgetLimit(String category, int amount, String id) {
        this.category = category;
        this.id = id;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
