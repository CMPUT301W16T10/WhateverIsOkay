package com.example.suhussai.gameshare;

/**
 * Created by suhussai on 12/02/16.
 */
public class Bid {

    // changed the borrower to bidder since it seems like that was the intention.
    private String bidder;

    private double amount;

    private Item item;

    public String getBidder() {
        return bidder;
    }

    public double getAmount() {
        return amount;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Bid(String bidder, double amount /*, Item item*/) {
        //this.item = item; //TODO: uncomment after test cases changed accordingly
        this.amount = amount;
        this.bidder = bidder;
    }

    // added so the list view looks better temporarily. TODO fix this
    public String toString(){
        return bidder + " bid " + Double.toString(this.getAmount());
    }
}
