package com.example.suhussai.gameshare;

/**
 * Class used to represent bids on items. A bid contains an amount (double) and a username (String)
 * A bid should always be contained by an item (It should only be created when placed into the
 * item's bid list)
 * @see Item
*/
public class Bid {

    /**
     * The username of the User making the bid
     */
    private String bidder;

    /**
     * The value of the bid placed
     */
    private double amount;

    /**
     * Gets the username of the User who placed the bid
     * @return username of bidder
     */
    public String getBidder() {
        return bidder;
    }

    /**
     * Gets the value of the bid
     * @return value of bid
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Constructor
     * @param bidder the username of the bidder
     * @param amount the amount of the bid
     */
    public Bid(String bidder, double amount /*, Item item*/) {
        //this.item = item; //TODO: uncomment after test cases changed accordingly
        this.amount = amount; //TODO: round to two decimal places
        this.bidder = bidder;
    }

    // added so the list view looks better temporarily. TODO fix this

    /**
     * Gets the information about the bid in a readable format
     * @return string with the bidder's username and the amount
     */
    public String toString(){
        return bidder + " bid " + Double.toString(this.getAmount());
    }
}
