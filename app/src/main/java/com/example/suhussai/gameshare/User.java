package com.example.suhussai.gameshare;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bobby on 11/02/16.
 */
public class User {
    private String username;
    private String name;
    private String phone;
    private String email;
    private String password;
    private ArrayList<Item> notifications = new ArrayList<Item>();
    private ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<Item> borrowedItems = new ArrayList<Item>();
    private String id;

    public User(){}


    public User(String userName, String password){
        this.username = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }
    public void setId(String id){this.id = id;}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNotifications(ArrayList<Item> notifications) {
        this.notifications = notifications;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setBorrowedItems(ArrayList<Item> borrowedItems) {
        this.borrowedItems = borrowedItems;
    }

    public ArrayList<Item> getBorrowedItems() {
        return borrowedItems;
    }

    public String getPassword() {
        return password;
    }

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
        // Set the item via the controller.
        ItemController.AddItem addItem = new ItemController.AddItem();
        addItem.execute(item);

        // Repopulate the item list owned by this user

        // Grab the items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(username);

        try {
            items = getItems.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(Item item){ items.remove(item);}

//    public void addOwnedItem(Item item){
//        items.add(item);
//    }

    public static User signIn(String username, String password) {
        return new User(username, password);
    }



    public void bidOn(Item item){
        //
    }

    public ArrayList<Item> getLentItems() {
        ArrayList<Item> beingBorrowed = new ArrayList<>();

        return beingBorrowed;
    }

    public void addBorrowedItem (Item item){
        this.borrowedItems.add(item);
    }

    public void declineBid(){

    }

    public void acceptBid(User winningUser) {
        /*
        Item item = bid.getItem();
        item.setStatus("borrowed");
        item.setRate(bid.amount);
        bid.getUser().addBorrowedItem(item);
         */
    }//TODO: change argument to take a Bid after test case has been changed accordingly
}
