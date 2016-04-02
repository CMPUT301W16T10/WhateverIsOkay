package com.example.suhussai.gameshare;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * The class used to represent items shared between users. Every item is associated with a user
 * (its owner), and has several fields used for information about it, as well as a list of bids.
 * @see User
 * @see Bid
 */
public class Item {
    /**
     * The item id used for elastic search
     */
    private String Id = "";
    /**
     * The name of the item
     */
    private String name;
    /**
     * The number of players
     */
    private String players = ""; //TODO Modify UML and/or related documents with the addition of 4 new String fields (or another datatype if necessary)
    /**
     * Age range
     */
    private String age = "";
    /**
     * The time required to play
     */
    private String timeReq = "";
    /**
     * The platform
     */
    private String platform ="";
    /**
     * Status, true if at least one bid is placed on the item
     * @see #borrowed
     */
    private boolean bidded = false;
    /**
     * Status, true if someone is borrowing the item
     * @see #bidded
     */
    private boolean borrowed = false;
    /**
     * True if a bid has been added since the last time the owner viewed the item
     */
    private boolean newBid = false;
    /**
     * The owner's username
     */
    private String owner;
    /**
     * The rate that the item will be borrowed at.
     */
    private double rate;
    /**
     * Username of the User borrowing the item
     */
    private String borrower;//TODO unset when item returned
    /**
     * List of all bids on the item (empty when borrowed)
     */
    private ArrayList<Bid> bids = new ArrayList<Bid>();
    /**
     * The location of item when borrowed is true. [long, lat]
     */
    private LatLng location;

    /**
     * The image for the item
     */
    private Photo image = new Photo(null);

    private Boolean updatedWhenOffline = false;


    // Overridden instantiation depending on the data provided in the construction.

    /**
     * Constructor
     * @param name the item's name
     * @param owner the owner's username
     * @param players the number of players
     * @param age the age range
     * @param timeReq the time requirement
     * @param platform the platform
     */
    public Item(String name, String owner, String players, String age, String timeReq, String platform) {
        this.name = name;
        this.owner = owner;
        this.players = players;
        this.age = age;
        this.timeReq = timeReq;
        this.platform = platform;
    }

    /**
     * Constructor
     * @param name the item's name
     * @param owner the owner's username
     */
    public Item(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    /**
     * Constructor
     * @deprecated
     */
    public Item(){}

    /**
     * Sets the rate of item to be borrowed.
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Checks if item has a new bid
     * @return true if new bids, else false
     */
    public boolean hasNewBid() {
        return newBid;
    }

    /**
     * Sets bids as viewed
     */
    public void setBidsViewed() {
        newBid=false;
    }

    /**
     * Set's the borrower's username (now done in accept bid method
     * @deprecated
     * @param borrower borrower's username
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * Gets the bids on the item
     * @return the list of bids
     */
    public ArrayList<Bid> getBids() {
        return bids;
    }

    /**
     * clears bids on item
     */
    public void clearBids(){
        bids.clear();
    }

    /**
     * Checks if someone is borrowing the item
     * @return true if borrowed, else false
     */
    public boolean isBorrowed() {
        return borrowed;
    }

    /**
     * @deprecated
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }//TODO: delete later after test case has been changed accordingly

    /**
     * Gets the item's owner
     * @return owner's username
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Adds a bid on the item
     * @param bid the bid to be added
     */
    public void addBid(Bid bid) {
        bids.add(bid);
        newBid=true;
        setBidded();
    }

    /**
     * sets the item's status to available
     */
    public void setAvailable(){
        borrowed = false;
        bidded = false;
        borrower = "";
        rate = 0;
    }

    /**
     * sets the item's status to borrowed
     */
    public void setBorrowed(){
        borrowed = true;
        bidded = false;
    }

    /**
     * sets the item's status to bidded
     */
    public void setBidded(){
        borrowed = false;
        bidded = true;
    }

    /**
     * Decline a bid made by a user, bid will be removed
     * @param bid the bid to decline
     */
    public void declineBid(Bid bid) {
        bids.remove(bid);
        // TODO notify bidder?

        if(bids.size() == 0) {
            setAvailable();
        }

        // update the info in the elastic search
        ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
        updateItem.execute(this);
    }

    /**
     * Gets the person borrowing the item
     * @return the borrower's username
     */
    public String getBorrower(){
        return borrower;
    }

    /**
     * Gets the rate of the borrowed item.
     * @return the item's borrowed rate in $/hr
     */
    public double getRate(){
        return rate;
    }

    /**
     * Gets the item's current status
     * @return string containing the status
     */
    public String getStatus(){
        if (borrowed){
            return "borrowed";
        }
        else if (bidded){
            return "bidded";
        }
        else{
            return "available";
        }
    }

    /**
     * Accept a bid on the item, all bids will be cleared and item set as borrowed
     * @param bid the bid to accept
     */
    public void acceptBid(Bid bid){
        borrower = bid.getBidder();
        bids.clear();
        setBorrowed();
        this.setRate(bid.getAmount());
        // update the info in the elastic search
        ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
        updateItem.execute(this);
    }

    /**
     * Checks if the item has been bidded on
     * @return true if item bidded on, else false
     */
    public boolean isBidded() {
        return bidded;
    }

    /**
     * @deprecated
     */
    public void setBidded(boolean bidded) {
        this.bidded = bidded;
    }

    /**
     * Gets the item's name
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the item's name
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the item's ID
     * @param ID the item's ID
     */
    public void setId(String ID){
        Id = ID;
    }

    /**
     * Gets the item's ID
     * @return the item's ID
     */
    public String getId() {
        return Id;
    }

    /**
     * Sets the number of players
     * @param players the number of players
     */
    public void setPlayers(String players) {
        this.players = players;
    }

    /**
     * Sets the age range
     * @param age the age range
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Sets the time requirement
     * @param timeReq the time requirement
     */
    public void setTimeReq(String timeReq) {
        this.timeReq = timeReq;
    }

    /**
     * Sets the platform
     * @param platform the platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Gets the geoLocation coordinates
     * @return location LatLng in (lat, long) format
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * Sets the geoLocation given the LatLng in (lat, long) format
     * @param location
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }


    /**
     * Gets the number of players
     * @return the number of players
     */
    public String getPlayers() {
        return players;
    }

    /**
     * Gets the age range
     * @return the age range
     */
    public String getAge() {
        return age;
    }

    /**
     * Gets the time requirement
     * @return the time requirement
     */
    public String getTimeReq() {
        return timeReq;
    }

    /**
     * Gets the platform
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Takes in a new Photo image and replaces existing image with the new value.
     * @param newImage the new Photo to replace this item's picture
     */
    public void addImage(Photo newImage){
        image = newImage;
    }

    /**
     * Returns the decoded bitmap image based on the base64 string associated
     * @return the decoded bitmap
     */
    public Bitmap getImage(){
        return image.getImage();
    }

    /**
     * Returns a boolean value indicating whether the current photo object contains any real image
     * @return boolean
     */
    public boolean hasImage() {
        if( image.getImageBase64().equals("") ) {
            return false;
        }
        else {
            return true;
        }
  }

    // added so the list view looks decent. TODO fix this

    /**
     * Returns basic information in a readable form
     * @return string with the name and owner's username in it
     */
    public String toString() {
        String returnString;
        if (UserController.getCurrentUser().getUsername().equals(this.owner)){
            returnString =  this.getName() + " (" + this.getStatus() + ")";
            if (newBid){
                returnString += " (New Bids)";
            }
        }
        else {
            returnString = this.getName() + " (" + this.getStatus() + ") owned by " + this.getOwner();
        }
        return returnString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        if (players != null ? !players.equals(item.players) : item.players != null) return false;
        if (age != null ? !age.equals(item.age) : item.age != null) return false;
        if (timeReq != null ? !timeReq.equals(item.timeReq) : item.timeReq != null) return false;
        if (platform != null ? !platform.equals(item.platform) : item.platform != null)
            return false;
        return !(owner != null ? !owner.equals(item.owner) : item.owner != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (timeReq != null ? timeReq.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    public Boolean getUpdatedWhenOffline() {
        return updatedWhenOffline;
    }

    public void setUpdatedWhenOffline(Boolean updatedWhenOffline) {
        this.updatedWhenOffline = updatedWhenOffline;
    }
}
