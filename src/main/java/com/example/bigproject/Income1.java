package com.example.bigproject;

import java.time.LocalDate;

public class Income1 extends FinancialTransaction {
    private String clientName;

    public Income1(int id, double amount, LocalDate date, String description, String clientName) {
        super(id, amount, date, description, description);
        this.clientName = clientName;
    }

    @Override
    public String getTransactionType() {
        return "";
    }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}