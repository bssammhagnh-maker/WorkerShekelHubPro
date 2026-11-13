package com.example.workershekel;

import java.time.LocalDate;

public class WorkerShift {
    private int id;
    private String workerName;
    private double hours;
    private double hourlyRate;
    private double totalShekels;
    private LocalDate date;
    private String notes;

    public WorkerShift(int id, String workerName, double hours, double hourlyRate, LocalDate date, String notes) {
        this.id = id;
        this.workerName = workerName;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.totalShekels = hours * hourlyRate;
        this.date = date;
        this.notes = notes;
    }

    public int getId() { return id; }
    public String getWorkerName() { return workerName; }
    public double getHours() { return hours; }
    public double getHourlyRate() { return hourlyRate; }
    public double getTotalShekels() { return totalShekels; }
    public LocalDate getDate() { return date; }
    public String getNotes() { return notes; }
}