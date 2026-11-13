package com.example.workershekel;

import java.time.LocalDate;

public class FinanceRecord {
    private int id;
    private String type;
    private String category;
    private double amount;
    private String description;
    private LocalDate date;

    public FinanceRecord(int id, String type, String category, double amount, String description, LocalDate date) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
}