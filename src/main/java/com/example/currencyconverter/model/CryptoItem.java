package com.example.currencyconverter.model;

public class CryptoItem {
    private String symbol;
    private String name;
    private double price;

    public CryptoItem(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
