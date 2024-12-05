package com.example.fireledger;
/**
 * Represents a billing item, including its details such as date, type, amount, etc.
 */
public class BillingItem {
    private String uuid; // Unique ID
    private String date; // Date of the billing item
    private String type; // Type of the billing item
    private String amount; // Amount of the billing item
    private String description; // Description of the billing item

    /**
     * Constructs a billing item with the provided information.
     *
     * @param uuid Unique identifier for the billing item
     * @param date Date of the billing item
     * @param type Type of the billing item
     * @param amount Amount of the billing item
     * @param description Description of the billing item
     */
    public BillingItem(String uuid, String date, String type, String amount, String description) {
        this.uuid = uuid;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    /**
     * Returns the unique identifier of the billing item.
     *
     * @return Unique identifier of the billing item
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Returns the date of the billing item.
     *
     * @return Date of the billing item
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the type of the billing item.
     *
     * @return Type of the billing item
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the amount of the billing item.
     *
     * @return Amount of the billing item
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Returns the description of the billing item.
     *
     * @return Description of the billing item
     */
    public String getDescription() {
        return description;
    }
}
