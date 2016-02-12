package com.example.bobby.exchangeapp;

/**
 * Created by dan on 2016-02-12.
 */
public class Bid {

    public Bid(User user, int amount, Item item){
        this.bidder=user;
        this.amount=amount;
        this.item = item;
    }

    public User getBidder() {
        return bidder;
    }

    public int getAmount() {
        return amount;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {

        return item;
    }

    private User bidder;

    private int amount;

    private Item item;

}
