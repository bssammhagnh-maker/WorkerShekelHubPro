package com.example.workershekel;

public class Worker {
    private int id;
    private String name;
    private double dailyWage;

    public Worker(int id, String name, double dailyWage) {
        this.id = id;
        this.name = name;
        this.dailyWage = dailyWage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDailyWage() {
        return dailyWage;
    }
}