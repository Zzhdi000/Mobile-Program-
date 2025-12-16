package com.example.financialapp;

public class Datacash {

    private String category;  // dipakai income/expense (atau jadi note untuk transfer)
    private String type;      // income / expense / transfer
    private String id;
    private String date;
    private int amount;
    private int month;
    private String currency = "";

    public Datacash() { }

    // CONSTRUCTOR ASLI untuk income & expense
    public Datacash(String category, String type, String id, String date, int amount, int month) {
        this.category = category;
        this.type = type;
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.month = month;
    }

    // ⭐ CONSTRUCTOR BARU UNTUK TRANSFER
    public Datacash(String id, String type, int amount, String note, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = note;   // NOTE disimpan di field category (biar tidak merusak UI)
        this.date = date;
        this.month = 0;         // optional (kalau tidak dipakai)
    }

    // GETTERS
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getId() { return id; }
    public String getDate() { return date; }
    public int getAmount() { return amount; }
    public int getMonth() { return month; }
    public String getCurrency() { return currency; }

    // SETTERS
    public void setCategory(String category) { this.category = category; }
    public void setType(String type) { this.type = type; }
    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setMonth(int month) { this.month = month; }
    public void setCurrency(String currency) { this.currency = currency; }
}
