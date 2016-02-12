package com.example.bobby.exchangeapp;

/**
 * Created by bobby on 11/02/16.
 */
public class Item {
    private String name;
    private boolean bidded = false;
    private boolean borrowed = false;

    private User borrower;

    private int rate;

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {

        return rate;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public User getBorrower() {

        return borrower;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

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
