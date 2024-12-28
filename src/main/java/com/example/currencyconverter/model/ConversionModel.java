package com.example.currencyconverter.model;

public class ConversionModel {

    private double originalAmount;
    private String originalCurrency;
    private double convertedAmount;
    private String targetCurrency;

    public ConversionModel(double originalAmount, String originalCurrency, double convertedAmount, String targetCurrency) {
        this.originalAmount = originalAmount;
        this.originalCurrency = originalCurrency;
        this.convertedAmount = convertedAmount;
        this.targetCurrency = targetCurrency;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }
}
