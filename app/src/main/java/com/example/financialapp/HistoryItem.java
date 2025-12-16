package com.example.financialapp;

public class HistoryItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TRANSACTION = 1;

    public int type;
    public String title;
    public Datacash data;

    public HistoryItem(int type, String title, Datacash data) {
        this.type = type;
        this.title = title;
        this.data = data;
    }
}

