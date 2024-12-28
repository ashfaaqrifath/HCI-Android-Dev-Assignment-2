package com.example.currencyconverter.model;

public class NewsItem {
    private String title;
    private String description;
    private String date;

    public NewsItem(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
