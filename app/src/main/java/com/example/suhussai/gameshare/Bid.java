package com.example.suhussai.gameshare;

/**
 * Created by suhussai on 12/02/16.
 */
public class Bid {

    private User borrower;

    private double amount;

    private Item item;

    public User getBorrower() {
        return borrower;
    }

    public double getAmount() {
        return amount;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Bid(User borrower, double amount /*, Item item*/) {
        //this.item = item; //TODO: uncomment after test cases changed accordingly
        this.amount = amount;
        this.borrower = borrower;
    }

    // added so the list view looks better temporarily. TODO fix this
    public String toString(){
        return borrower.getUsername() + " bid " + Double.toString(this.getAmount());
    }
}
