package com.example.financialapp;

public class Datacash {

    private String category;
    private String type;
    private String id;
    private String date;
    private int amount;
    private int month;
    private String currency = "";   // FIX: default kosong agar tidak override UI

    public Datacash() { }

    public Datacash(String category, String type, String id, String date, int amount, int month) {
        this.category = category;
        this.type = type;
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.month = month;
    }

    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getId() { return id; }
    public String getDate() { return date; }
    public int getAmount() { return amount; }
    public int getMonth() { return month; }
    public String getCurrency() { return currency; }

    public void setCategory(String category) { this.category = category; }
    public void setType(String type) { this.type = type; }
    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setMonth(int month) { this.month = month; }
    public void setCurrency(String currency) { this.currency = currency; }
}
