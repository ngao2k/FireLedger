package com.example.fireledger;

public class BillingItem {
    private String date;
    private String type;
    private String amount;
    private String description;

    public BillingItem(String date, String type, String amount, String description) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
