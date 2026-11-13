package com.example.workershekel;

import java.time.LocalDate;

public class AttendanceRecord {
    private int id;
    private String workerName;
    private String status;
    private LocalDate date;

    public AttendanceRecord(int id, String workerName, String status, LocalDate date) {
        this.id = id;
        this.workerName = workerName;
        this.status = status;
        this.date = date;
    }

    public int getId() { return id; }
    public String getWorkerName() { return workerName; }
    public String getStatus() { return status; }
    public LocalDate getDate() { return date; }
}