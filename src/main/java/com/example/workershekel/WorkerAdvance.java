package com.example.workershekel;

import java.time.LocalDate;

public class WorkerAdvance {
    private int id;
    private String workerName;
    private double amount;
    private LocalDate date;
    private String notes;

    public WorkerAdvance(int id, String workerName, double amount, LocalDate date, String notes) {
        this.id = id;
        this.workerName = workerName;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    public int getId() { return id; }
    public String getWorkerName() { return workerName; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getNotes() { return notes; }
}