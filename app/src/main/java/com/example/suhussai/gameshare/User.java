package com.example.suhussai.gameshare;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bobby on 11/02/16.
 */
public class User {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private ArrayList<Item> notifications = new ArrayList<Item>();
    private ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<Item> borrowedItems = new ArrayList<Item>();

    private int gameCount = 0;

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

    public int getGameCount() {
        // allows a game to have a unique ID
        return gameCount;
    }

    protected void incrementGameCount() {
        // no need for a set gameCount because it always begins at zero and goes up by one
        // allows a game to have a unique ID
        // can only be called internal to the class or any subclass that may exist in future.
        gameCount++;
    }

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

    public Item getItem(int pos){
        return items.get(pos);
    }

    public ArrayList<Bid> getCurrentBids(Item item){
        return new ArrayList<Bid>();
    }

    public ArrayList<Item> getOwnedBorrowedItems() {
        return new ArrayList<Item>();
    }

    public ArrayList<Item> getOwnedAvailableItems() {
        return new ArrayList<Item>();
    }

    public void markItemReturned(Item item){

    }

    public void addItem(Item item){

        // first increment the gameCount so the new item has the newly incremented game value.
        incrementGameCount();

        // Refresh the item list owned by this user in case of any recent changes
        // Grab the items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(getItems.MODE_GET_MY_ITEMS, username);

        UserController.setCurrentUser(this);

        try {
            items = getItems.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // set to item's ID before sending to controller
        item.setId(getUsername() + (char) 31 + getGameCount());
        items.add(item);

        // Post the new item information
        // Set the item via the controller.
        ItemController.AddItem addItem = new ItemController.AddItem();
        addItem.execute(item);

        // Adds the Item to the user
        UserController.UpdateUserProfile updateUserProfile = new UserController.UpdateUserProfile();
        updateUserProfile.execute(this);
    }

    public void deleteItem(Item item){
        ItemController.DeleteItem deleteItem = new ItemController.DeleteItem();
        deleteItem.execute(item);
        items.remove(item);
    }


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
        ArrayList<Item> beingBorrowed = new ArrayList<Item>();

        return beingBorrowed;
    }

    public void addBorrowedItem (Item item){
        this.borrowedItems.add(item);
    }

    public void declineBid(Bid bid, Item item){
        item.declineBid(bid);
    }

    public void acceptBid(Bid bid, Item item) {
        item.acceptBid(bid);
    }
}
