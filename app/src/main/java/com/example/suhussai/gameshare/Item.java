package com.example.suhussai.gameshare;

import android.app.AlertDialog;

import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by bobby on 11/02/16.
 */
public class Item {


    private String name = "";
    private String players = ""; //TODO Modify UML and/or related documents with the addition of 4 new String fields (or another datatype if necessary)
    private String age = "";
    private String timeReq = "";
    private String platform ="";
    private boolean bidded = false;
    private boolean borrowed = false;
    private User owner;
    private double rate;
    private User borrower;
    private ArrayList<Bid> bids = new ArrayList<Bid>();
    @JestId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Overridden instantiation depending on the data provided in the construction.
    public Item(String name, User owner, String players, String age, String timeReq, String platform) {
        this.name = name;
        this.owner = owner;
        this.players = players;
        this.age = age;
        this.timeReq = timeReq;
        this.platform = platform;
    }

    public Item(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public void clearBids(){
        bids.clear();
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }//TODO: delete later after test case has been changed accordingly


    public User getOwner() {
        return owner;
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public void setAvailable(){
        borrowed = false;
        bidded = false;
    }

    public void setBorrowed(){
        borrowed = true;
        bidded = false;
    }
    public void setBidded(){
        borrowed = false;
        bidded = true;
    }

    public void declineBid(Bid bid) {

    }//TODO: delete later after test case has been changed accordingly

    public User getBorrower(){
        return borrower;
    }

    public double getRate(){
        return rate;
    }

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

    public void acceptBid(Bid bid){

    } //TODO: delete later after test case has been changed accordingly

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

    // added so the list view looks decent. TODO fix this
    @Override
    public String toString() {
        return this.getName() + " owned by ";
    }

}
