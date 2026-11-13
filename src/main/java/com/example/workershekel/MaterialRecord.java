package com.example.workershekel;

import java.time.LocalDate;

public class MaterialRecord {
    private int id;
    private String materialName;
    private String quantity;
    private double cost;
    private String supplier;
    private LocalDate date;

    public MaterialRecord(int id, String materialName, String quantity, double cost, String supplier, LocalDate date) {
        this.id = id;
        this.materialName = materialName;
        this.quantity = quantity;
        this.cost = cost;
        this.supplier = supplier;
        this.date = date;
    }

    public int getId() { return id; }
    public String getMaterialName() { return materialName; }
    public String getQuantity() { return quantity; }
    public double getCost() { return cost; }
    public String getSupplier() { return supplier; }
    public LocalDate getDate() { return date; }
}