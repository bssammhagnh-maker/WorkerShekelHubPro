package com.example.bigproject;

import java.time.LocalDate;

public class Income extends FinancialTransaction {
    public Income(int id, double amount, LocalDate date, String description, String category) {
        super(id, amount, date, description, category);
    }

    @Override
    public String getTransactionType() {
        return "إيرادات";
    }
}
