package com.example.bigproject;

import java.time.LocalDate;

public abstract class FinancialTransaction {
    private int id;
    private double amount;
    private LocalDate date;
    private String description;

    public FinancialTransaction(int id, double amount, LocalDate date, String s, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public abstract String getTransactionType();
}