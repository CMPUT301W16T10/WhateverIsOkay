package com.example.suhussai.gameshare;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class User {
    private String username;
    private String email;
    private ArrayList<Item> notifications = null;

    public ArrayList<Item> getNotifications() {
        return notifications;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private ArrayList<Item> items = null;

    public ArrayList<Item> getItems() {
        return items;
    }

    public Item getItem(Item item){
        return null;
    }

    public ArrayList<Bid> getCurrentBids(Item item){
        return new ArrayList<>();
    }

    public ArrayList<Item> getOwnedBorrowedItems() {
        return new ArrayList<>();
    }
    public ArrayList<Item> getOwnedAvailableItems() {
        return new ArrayList<>();
    }
    public void markItemReturned(Item item){

    }

    public void addItem(Item item){
        items.add(item);
    }
    public void deleteItem(Item item){ items.remove(item);}
    public void addOwnedItem(Item item){
        items.add(item);
    }

    public static User signIn(String username, String password) {
        return new User(username, password);
    }

    public User(String userName, String password){
        // check if user exists
    }

    public User(String userName){
        // check if user exists
    }


    public void bidOn(Item item){
        //
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>();
    }

    public ArrayList<Item> getItemsBorrowed() {
        ArrayList<Item> borrowed = new ArrayList<>();

        return borrowed;
    }

    public ArrayList<Item> getItemsBeingBorrowed() {
        ArrayList<Item> beingBorrowed = new ArrayList<>();

        return beingBorrowed;
    }

    public void acceptBid(User winningUser) {

    }
}
