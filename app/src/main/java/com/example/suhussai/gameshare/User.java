package com.example.suhussai.gameshare;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Class representing a user of the application. Each user has some fields containing profile
 * information, as well as lists of items they own, items they are borrowing, and notifications
 * @see Item
 */
public class User {
    /**
     * The user's username
     */
    private String username;
    /**
     * The user's password
     */
    private String password;
    /**
     * The user's name
     */
    private String name;
    /**
     * The user's email
     */
    private String email;
    /**
     * The user's phone number
     */
    private String phone;
    /**
     * List of notifications
     */
    private ArrayList<Item> notifications = new ArrayList<Item>();
    /**
     * List of owned items
     */
    private ArrayList<Item> items = new ArrayList<Item>();
    /**
     * List of borrowed items
     */
    private ArrayList<Item> borrowedItems = new ArrayList<Item>();
    /**
     * Total number of games user has had over all time (doesn't decrement on game deletion)
     */
    private int gameCount = 0;

    private Boolean updatedWhenOffline = false;

    /**
     * Constructor
     */
    public User(){}

    /**
     * Constructor
     * @param username the username
     * @param password the password
     */
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the user's name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's phone number
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number
     * @param phone  the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * Gets the user's game count
     * @return the game count
     */
    public int getGameCount() {
        // allows a game to have a unique ID
        return gameCount;
    }

    /**
     * increments the user's game count (called whenever game is added)
     */
    protected void incrementGameCount() {
        // no need for a set gameCount because it always begins at zero and goes up by one
        // allows a game to have a unique ID
        // can only be called internal to the class or any subclass that may exist in future.
        gameCount++;
    }

    /**
     * Sets the user's password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user's notifications
     * @param notifications the notifications
     */
    public void setNotifications(ArrayList<Item> notifications) {
        this.notifications = notifications;
    }

    /**
     * Sets the user's items
     * @param items the items
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    /**
     * Sets the user's borrowed items
     * @param borrowedItems the items
     */
    public void setBorrowedItems(ArrayList<Item> borrowedItems) {
        this.borrowedItems = borrowedItems;
    }

    /**
     * Gets the user's borrowed items
     * @return the items
     */
    public ArrayList<Item> getBorrowedItems() {
        return borrowedItems;
    }

    /**
     * Gets the user's password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's notifications
     * @return the notifications
     */
    public ArrayList<Item> getNotifications() {
        return notifications;
    }

    /**
     * Gets the user's username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username
     * @param username the username
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets the user's email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's items
     * @return the items
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Gets the user's nth item
     * @param pos the index to get
     * @return the item
     */
    public Item getItem(int pos){
        return items.get(pos);
    }

    /**
     * Gets the user's current bids
     * @return the bids
     */
    public ArrayList<Bid> getCurrentBids(Item item){
        return new ArrayList<Bid>();
    }

    /**
     * Gets the user's items someone else is borrowing
     * @return the items
     */
    public ArrayList<Item> getOwnedBorrowedItems() {
        ArrayList<Item> AL = new ArrayList<Item>();
        for( Item i : items ) {
            if( i.isBorrowed() ) {
                AL.add(i);
            }
        }
        return AL;
    }

    /**
     * Gets the user's items which are available
     * @return the items
     */
    public ArrayList<Item> getOwnedAvailableItems() {
        ArrayList<Item> AL = new ArrayList<Item>();
        for( Item i : items ) {
            if( !i.isBorrowed() && !i.isBidded() ) {
                AL.add(i);
            }
        }
        return AL;    }

    /**
     * Sets the item as having been returned
     * @param item the item
     */
    public void markItemReturned(Item item){
        item.setAvailable();
    }

    /**
     * Adds a new item to the user
     * @param item the new item
     */
    public void addItem(Item item){

        if (items.contains(item)) {
            return;
        }

        // first increment the gameCount so the new item has the newly incremented game value.
        incrementGameCount();

        if (item.getId().contains("NO_INTERNET")) {
            //  don't change id
            items.add(item);
        }
        else {

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

    }

    /**
     * Deletes the specified item
     * @param item the item
     */
    public void deleteItem(Item item){
        if (items.contains(item)) {
            ItemController.DeleteItem deleteItem = new ItemController.DeleteItem();
            deleteItem.execute(item);
            items.remove(item);
        }
    }

    /**
     * Sign in to the application
     * @param username the username
     * @param password the password
     * @return The User that has been logged in as
     */
    public static User signIn(String username, String password) {
        return new User(username, password);
    }

    /**
     * Decline bid on an owned item
     * @param bid the bid
     * @param item the item
     */
    public void declineBid(Bid bid, Item item){
        item.declineBid(bid);
    }

    /**
     * Accept bid on an owned item
     * @param bid the bid
     * @param item the item
     */
    public void acceptBid(Bid bid, Item item) {
        item.acceptBid(bid);
    }

    /**
     * gets updatedWhenOffline
     * @return updatedWhenOffline
     */
    public Boolean getUpdatedWhenOffline() {
        return updatedWhenOffline;
    }

    /**
     * sets updatedWhenOffline
     */
    public void setUpdatedWhenOffline(Boolean updatedWhenOffline) {
        this.updatedWhenOffline = updatedWhenOffline;
    }

    /**
     * Compares an Item with the current Item
     * @param o the Object
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;
        return !(password != null ? !password.equals(user.password) : user.password != null);

    }
    /**
     * Generates a unique id for the user.
     * @return result
     */
    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
