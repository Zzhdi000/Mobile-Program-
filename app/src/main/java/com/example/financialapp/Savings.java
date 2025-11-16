package com.example.financialapp;

public class Savings {

    private String id;
    private long amount;
    private String date;
    private int month;
    private String note;

    public Savings() {
        // required for Firebase
    }

    // Constructor that your code in SavingsActivity uses:
    public Savings(String id, long amount, String date, int month, String note) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.month = month;
        this.note = note;
    }

    // --- Getter used by Adapter ---
    public String getId() { return id; }
    public long getAmount() { return amount; }
    public String getDate() { return date; }
    public int getMonth() { return month; }
    public String getNote() { return note; }

    // --- Setter if firebase needs it ---
    public void setId(String id) { this.id = id; }
    public void setAmount(long amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setMonth(int month) { this.month = month; }
    public void setNote(String note) { this.note = note; }
}
