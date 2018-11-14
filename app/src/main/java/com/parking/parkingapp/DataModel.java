package com.parking.parkingapp;

public class DataModel {

    String name;
    String status;
    String totalAmount;
    String amountLeft;

    public DataModel(String name, String status, String totalAmount, String amountLeft) {
        this.name = name;
        this.status = status;
        this.totalAmount = totalAmount;
        this.amountLeft = amountLeft;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getAmountLeft() {
        return amountLeft;
    }
}
