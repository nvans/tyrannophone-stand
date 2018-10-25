package com.nvans.tyrannophone.stand.model;

public class Option {

    private String name;

    private Integer price;

    private boolean isConnected;


    // Getters and setters -->
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    // <-- Getters and setters

}
