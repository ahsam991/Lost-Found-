package com.campus.lostfound.model;

public abstract class BaseItem {
    private String title;
    private String category;
    private String brand;
    private String model;
    private String color;
    private String serialNumber;
    private String description;

    public BaseItem() {}

    public BaseItem(String title, String category, String brand, String model, String color, String serialNumber, String description) {
        this.title = title;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.serialNumber = serialNumber;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
