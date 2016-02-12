package com.example.bobby.exchangeapp;

/**
 * Created by bobby on 11/02/16.
 */
public class Item {
    private String name;
    private boolean bidded = false;
    private boolean borrowed = false;
    public String status;

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public Item(String name) {
        this.name = name;
        this.bidded = false;
    }

    public boolean isBidded() {
        return bidded;
    }

    public void setBidded(boolean bidded) {
        this.bidded = bidded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
