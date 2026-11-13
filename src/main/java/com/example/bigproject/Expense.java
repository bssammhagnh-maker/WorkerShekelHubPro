package com.example.bigproject;

import java.time.LocalDate;

public class Expense extends FinancialTransaction {
    public Expense(int id, double amount, LocalDate date, String description, String category) {
        super(id, amount, date, description, category);
    }

    @Override
    public String getTransactionType() {
        return "مصاريف";
    }
}
