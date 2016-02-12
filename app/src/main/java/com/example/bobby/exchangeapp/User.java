package com.example.bobby.exchangeapp;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class User {

    private String username;

    public String getUsername(){
        return username;
    }

    private ArrayList<Item> items = null;

    public ArrayList<Item> getItems() {
        return items;
    }

    private ArrayList<Item> notifications = null;

    public ArrayList<Item> getNotifications() {
        return notifications;
    }

    public void addItem(Item item){
        items.add(item);
    }

    public static User signIn(String username, String password) {
        return new User(username, password);
    }

    public User(String userName, String password){
        // check if user exists
    }

    public Bid bidOn(Item item, int price){
        //
        return new Bid(this, price, item);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>();
    }

    public ArrayList<Item> getItemsBorrowed() {
        ArrayList<Item> borrowed = new ArrayList<>();

        return borrowed;
    }

    public ArrayList<Item> getCurrentBids() {
        ArrayList<Item> bids = new ArrayList<>();

        return bids;
    }

    public ArrayList<Item> getItemsBeingBorrowed() {
        ArrayList<Item> beingBorrowed = new ArrayList<>();

        return beingBorrowed;
    }

    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> available = new ArrayList<>();

        return available;
    }

    public void declineBid(Bid bid){

    }

    public void acceptBid(Bid bid) {

    }

    public void markItemReturned(Item item){

    }
}
