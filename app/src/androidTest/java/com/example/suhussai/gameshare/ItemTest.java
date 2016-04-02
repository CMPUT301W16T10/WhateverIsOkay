package com.example.suhussai.gameshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bobby on 11/02/16.
 */
public class ItemTest extends ActivityInstrumentationTestCase2 {
    public ItemTest() {
        super(Item.class);
    }

    private User user = null;
    private Item item1 = null;
    private Item item2 = null;
    String name1 = null;
    String name2 = null;
    String username = null;
    String password = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        name1 = "Monopoly";
        name2 = "Risk";
        username = "test_user";
        password = "1";

        user = new User(username, password);
        UserController.setCurrentUser(user);

        item1 = new Item(name1, username);
        item2 = new Item(name2, username);
        item1.setAvailable();
        item2.setAvailable();

        user.addItem(item1);
        user.addItem(item2);

    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        user = UserController.getCurrentUser();
        if (user.getItems().contains(item1)) {
            user.deleteItem(item1);
        }
        if (user.getItems().contains(item2)) {
            user.deleteItem(item2);
        }

        user = null;
        item1 = null;
        item2 = null;
    }

    // UC 11
    public void testAddItem() {
        Item item = new Item("new item", "BigOwner");

        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertTrue(user.getItems().contains(item));
    }

    // UC 12
    public void testViewItem() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 13
    public void testSelectItem() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 14
    public void testEditItem() {
        assertEquals(user.getItem(0).getName(), name2);
        user.getItem(0).setName("new_name");
        assertEquals(user.getItem(0).getName(), "new_name");
    }

    // UC 15
    public void testDeleteItem() {
        user.addItem(item1);
        assertTrue(user.getItems().contains(item1));
        user.deleteItem(item1);
        assertFalse(user.getItems().contains(item1));

    }

    // US 02.01.01
    public void testStatus() {
        // test for user story 02.01.01, must be true in all application states for all items
        assertTrue((item2.getStatus() == "available"));
        assertTrue((item1.getStatus() == "available"));
    }


    // UC 31
    public void testUserProfile(){
        // This Use Case deals solely with UI interface (uniqueness, etc.), not implemented in Unit Tests
    }

    // UC 32
    public void testUpdateProfile(){
        // As a user, I want to edit the contact information in my profile.
        String username = "thinglover";
        String password = "pass1";
        String email = "email@something.com";
        String newEmail = "mail@something.com";
        String name = "Joe";
        String newName = "Joey";
        String phoneNumber = "91";
        String newPhoneNumber = "911";

        User user = new User(username, password);
        assertNotNull(user);
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phoneNumber);

        assertEquals(user.getEmail(), email);
        assertEquals(user.getName(), name);
        assertEquals(user.getPhone(), phoneNumber);
        assertEquals(user.getUsername(), username);

        user.setEmail(newEmail);
        user.setName(newName);
        user.setPhone(newPhoneNumber);

        assertEquals(user.getEmail(), newEmail);
        assertEquals(user.getName(), newName);
        assertEquals(user.getPhone(), newPhoneNumber);
        assertEquals(user.getUsername(), username);
    }

    // UC 33
    public void testGetOwnerInfo(){
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 41
    public void testSearchKeyword(){
        // US 04.01.01
        // As a borrower, I want to specify a set of keywords, and search for all things not currently borrowed whose description contains all the keywords.
        // US 04.02.01
        // As a borrower, I want search results to show each thing not currently borrowed with its description, owner username, and status.

        String keyword = "Monoply";

        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(ItemController.GetItems.MODE_SEARCH_KEYWORD, keyword);
        boolean containsItemWithKeyWord = false;
        try {
            for (Item item : getItems.get()){
                assertNotSame(item.getStatus(), "Borrowed"); // make sure item not borrowed
                if (item.getName().contains(keyword)) {
                    containsItemWithKeyWord = true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertTrue(containsItemWithKeyWord);

    }

    // UC 42
    public void testSearchAllThings() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 51
    public void testBid(){
        // test for use case 05.01.01
        /*user.bidOn(item1);
        assertTrue(item1.isBidded());
        user.bidOn(item2);
        assertTrue(item2.isBidded());*/
        User bidder = new User("name","pass");
        Item item = new Item("game","name");
        assertFalse(item.isBidded());
        Bid bid = new Bid(bidder.getUsername(),1.0);
        item.addBid(bid);
        item.setBidded();
        assertTrue(item.isBidded());
        assertTrue(item.getBids().contains(bid));
    }

    // UC 52
    public void testSeePendingItems() {
        //should adjust to use setUp();
        // test for use case 05.02.01
        ArrayList<Item> pendingItems = new ArrayList<Item>();
        User user = new User("user","pass");
        Item item1 = new Item("item1",user.getUsername());
        item1.setBidded();
        user.addItem(item1);
        if (item1.getStatus() == "bidded")
            pendingItems.add(item1);
        assertEquals(pendingItems.size(), 1);
    }

    // UC 53
    public void testNotification(){
        // test for use case 05.03.01
        //should use setUp() parameters
        User user = new User("user","pass");
        Item item = new Item("item","user");
        User user2 = new User("user2","pass2");
        user.addItem(item);
        Bid bid = new Bid("user2",1.0);
        item.addBid(bid);
        user.acceptBid(bid, item);
        //Notifications not implemented yet, so this will fail
        assertTrue(user.getNotifications().contains("Bid on item by " + user2.getUsername()));
    }

    // UC 54
    public void testViewBids() {
        //should adjust to use setUp();

        // test for use case 05.04.01
        item1 = new Item("Monopoly","steve");
        item2 = new Item("Risk","steve");
        item1.addBid(new Bid("user2",12.0));
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(item1);
        items.add(item2);
        ArrayList<Item> bidded = new ArrayList<Item>();
        // TODO add get bidded items from user class as a method
        for( Item i : items ) {
            if( i.getStatus() == "bidded" ) {
                bidded.add(i);
            }
        }
        assertTrue(bidded.contains(item1));
        assertFalse(bidded.contains(item2));
    }

    // UC 55
    public void testBidsOnItems() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 56
    public void testAcceptOfferOnMyThing(){
        //should use setUp();
        String name = "Monoply"; // some meaningful item name
        String username = "user1"; //some meaningful username
        String username2 = "user2"; //some other user
        String username3 = "user3"; //third user

        double amount = 1.46;
        double amount2 = 1.12;

        User user = new User( username , "");
        Item item = new Item( name ,username);
        User borrower = new User( username2 , "");

        Bid bid = new Bid( username2, amount );
        Bid bid2 = new Bid( username3, amount2 );
        user.addItem(item);
        item.addBid(bid);
        item.addBid(bid2);
        assertEquals(item.getStatus(), "bidded" );
        user.acceptBid(bid, item);
        assertEquals(item.getStatus(), "borrowed");
        assertFalse(item.getBids().contains(bid)); //ensure the item’s accepted bid is no longer in current bid list
        assertFalse(item.getBids().contains(bid2)); //ensure the item’s current bids do not include the second, auto declined bid.
        assertEquals(item.getBorrower(), borrower.getUsername());
        assertEquals(item.getRate(), amount );
    }

    // UC 57
    public void testDeclineOfferOnMyThing(){
        // should use setUp();
        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username
        String username2 = "Joe.Stevens"; //some other user
        double amount = 1.46;
        User user = new User( username , "");
        Item item = new Item( name ,username);
        User borrower = new User( username2 , "");
        user.addItem(item);
        Bid bid = new Bid( borrower.getUsername(), amount);
        item.addBid(bid);

        assertTrue(item.getBids().contains(bid));
        user.declineBid(bid, item);
        assertFalse(item.getBids().contains(bid));
        assertEquals(item.getStatus(), "available");
    }

    // UC 61
    public void testViewBorrowedItems() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 62
    public void testViewItemsBeingBorrowed() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 71
    public void testMarkMyThingsReturnedAndAvailable() {
        //should adjust to use setUp();

        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username

        User user = new User( username , "");
        Item item = new Item( name,user.getUsername() );
        item.setBorrowed(); //item is now borrowed

        user.addItem(item ); //user owns borrowed item

        assertTrue(user.getOwnedBorrowedItems().contains(item));

        assertFalse( user.getOwnedAvailableItems().contains( item ) );

        assertEquals( item.getStatus(),"borrowed" ); //redundant check

        user.markItemReturned( item );

        assertTrue( user.getOwnedAvailableItems().contains( item ) );

        assertFalse(user.getOwnedBorrowedItems().contains(item));

        assertEquals(item.getStatus(), "available"); //redundant check
    }

    // UC 81
    public void testConnectivityPush() {
        //should adjust to use setUp();
        User user = new User("user1", "");
        Item item = new Item("Risk",user.getUsername());
        // disableConnection();//some method that simulates user being offline
        user.addItem(item);
        // enableConnection();//undoes the disableConnection method
        ArrayList<Item> items = new ArrayList<Item>();
        ArrayList<Item> userItems = new ArrayList<Item>();
        userItems = user.getItems();
        int size = userItems.size() - 1; //the last item in the list
        Item theItem = user.getItem(size);
        items.add(theItem);
        assertTrue(items.contains(item));
    }

    // UC 91
    public void testAddPhoto() {
        //TODO set up this test
        // Issue: Photos are of an unknown object type at this point. Unable to implement a unit test for this before investigating how
        // We are likely to implement the functions
    }


    // UC 92
    public void testDeletePhoto() {
        //TODO set up this test
        // Issue: Photos are of an unknown object type at this point. Unable to implement a unit test for this before investigating how
        // We are likely to implement the functions
    }

    // UC 93
    public void testViewPhoto() {
        //TODO set up this test
        // Issue: Photos are of an unknown object type at this point. Unable to implement a unit test for this before investigating how
        // We are likely to implement the functions
    }

    // UC 94
    public void testCheckPhotoSize(){

        String imageBase64;
        imageBase64 = "iVBORw0KGgoAAAANSUhEUgAAALQAAABqCAIAAACam+w6AAAAA3NCSVQICAjb4U/gAAAgAElEQVR4nFy8WY9lWXYetoa99xnuGHNm5JyV1";
        imageBase64 += "TV1d3WL7CYp0qRICRYpwIJlQC+C/SNs+M36CYbeDfjVD4Yf";
        imageBase64 += "CEgwBImkKEqkyJ6H6qrqmiurMmOOuOMZ995r+WHfG1XyeQhk3BuIyLPOGr71fd++uFwuX7w4idHP5/N";
        imageBase64 += "79+8XZSEhRpXQ+08/eX54uH9wdLheLxGh77yq5EV+czMjpBjjer2+c+coxrheV03TLBbLqqoGg2K1WiNiCKF";
        imageBase64 += "vuyC+78N8Pj842FutVhJjs1oSIRETMxC2dVet18Nh6ZxVBUIERABQEABkYkRUVQBBBERWBQBgZiKEGIFAgQBQtxcCgKh1hpE+/";
        imageBase64 += "NHPPvjZ+0XplNQo+xiiKgOKiu86ICI21lBm2CkiAiAggAXICTJVAiUEBVIiUGEmkIhAzKbruw6kXrfO5iH0YCw52/i+EJAYYVgik/QRASRoIyEwCYCztizz0Ieq7TFzLjMOYWQMxXC+6l";
        imageBase64 += "9E7A1bw0AkItZaYlJRHyTPsxglSjBEV7ObvuuJDQAAIBEGCRJEAAjIEAioKkoMEiMQqSIBsCFEUEAEtIYH49I4tIYNW0BkRFEFQo1CBhGYifDs7MwYW5bZbLYIwRdF3odIaH72s58e7B9";
        imageBase64 += "Mp2Mvsl4uDw+OrDXMGKL0vV8tV13XsOXlfAUgQWV2M1/czIlJJI6nE9/1IYSu67z363V17/juz3/244M7d52zy/lCJWoIMQYAZLYhemMYAYkZABBSegAiQQoAIoASkaqqCiEjIgEgYgQl";
        imageBase64 += "QgBCBEBSjc355Wc/+WXW9aNBkRFL68V7awwSgSoqCgGoZECGWQwDkGUoDFuybd/P6ioYc2c8OiozC8pRmW1mHLOigjNkGIMoEC0WjcnMuuoE4nA47LsABNEHYhOI2+DFB2eMiFYhLAlWwTtrc";
        imageBase64 += "2MBoLemYwCBSVEcGYvz2QcXlz9Y1S+8sAHLhogAwBgDAFEFEUWEkQXk6vomxiiABjnEoAiEgACiqoJMIKoxKhOpiGhERARWFQVBImbKM1sUucuNMQjAiJj+UKquzDkfIwLg+fm5994YjlGm08mHH";
        imageBase64 += "348nowHwzKzmbWubpooMcX+vXffW63Xx8fHAOCcy/P85PT08uJqOBzs7k5fvHhR1/WTp0/apv3oo09ExFpCpKqpm6r23hd5rhqXs3k+yGPfWzJszXK5RMQYA7NhZtg+clFFBETClCYAAOkVTvcAAM";
        imageBase64 += "xokKJE3eQQKmh+cXXnahm6LvpoDTkio+yKHAFVJMaACBCFEA0bzrLOGImxQC0ymxXOR62bIIQyGebDYrRcx+iZrQJkDBQ1F0GV6KPXWK07YIoSYlAkNLkTHz+fL9zfe/vZ7/72aDz58Z/+m5Kxu7x++";
        imageBase64 += "rt//7MPP975xiuHz56CyKefff7L999bNPX3v/9bv/3971393Q+e/+v/9wXKp/uH//rf/PvheJzlWaoEUU3NAwkBQFUQzPX1ddO1zJQ6q4ICQAgBCRFQFUBVVAEQQRFUAVQACRQAACyzdVyWuXHMhh";
        imageBase64 += "HQOQcA3ntjjKo6xyIIAPw//k//ApHyPBeRpu2t4eFgEKMA4Onp6Xy+sMa4zAHCwcHBcDjo+34+X7ZtG0UQ8d79ewr6ox/+qGna3f29qm7qph6UZVkWdV33fe+sJaK2bV9/842ry+vd6TT4wITWma";
        imageBase64 += "bpJzs7TVOHGIwxuG0XgJhafHoFEUEVUBEQNX2PRESAKkpsgAAUFUBBp21/WPcFoiXKrM2IDFGRZZk1hOgYSTHPssLajKkHfbk/3f39v28f3DtdrT6q6/DslTf+yX+r49EnH344evBg8A9+73Nrs2++";
        imageBase64 += "df8Pf7958OD9emXrZooATCIRIhhnM2sQZFDkmTGWYNb3L1mHd452jg6/fPfdg91p03TXvu3PLv1qWRzt3lxff/6rX/jT0zKC0Vidn+LFub24rILU5eCj51+SzYgJCAFEohISKBCnqmE20DRdlIjI";
        imageBase64 += "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";
        imageBase64 += "FUFeQMfb45Um9NAt+3zEzNQggAJCLEPVurvdpUejwawN1DfSWYcdd1KWnwJBbXfYUQcs6r1WowGLietG3b3d1dWxso3BoZAjuTklK7XCxKKqGKgNbkzu126hhbyWrSzTvRgkyRqJSSs99TiASG";
        imageBase64 += "RmsjxhrbMQBgckE5fMI/ORprmkpZiVgI0KbmbAb7T37l3/yrG5tbP/j6/4Hj09/5nX/+X3//fyBoRSEBBQDkGvvQLDImdQOVAzlIwBimG1yPpE2yOCtlBQqYgAooKBCBIWSxnM2TsQP7BQ6m4BAGEQYahI3ermhKFVEcas4gCgrgCvS1qx17kZ7zGn1Uok/uZggiYAyEhe39J4d/95ePujwif+bMikrwviwCNDJTAeu6TvzqNwEjMyBydwL0Yh/wi8mbY7lHUXvfNKgJhLVYUBUDM1MpZTmbE9H/B9cIoyzUucCoAAAAAElFTkSuQmCC";
        byte[] decodeString = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        double bytes = image.getAllocationByteCount();
        double max_size = 65536; // == 512 kb
        assertTrue("size is less than than 65536 bytes.", bytes >= max_size);
        Photo imagePhoto = new Photo(image);
        Bitmap smallImage = imagePhoto.getImage();
        double newBytes = smallImage.getByteCount();
        assertTrue("size is greater than than 65536 bytes.", newBytes < max_size);
    }

    // UC 101
    // Need to implement GeoJson - Part to be implemented for Project Part 5
    // & implement google maps: http://www.tutorialspoint.com/android/android_google_maps.htm
    public void testMarkGeoLocation(){
        String coordinates = "[-113.52715, 53.52676]"; // long , lat
        // Use GoogleMap to pinpoint the coordinates (CSC)
        String coordinatesReturned = "returned coordinates from GoogleMap after location set";
        assertEquals(coordinates, coordinatesReturned);
    }
    // UC 102
    // Needs same implementation as UC 101
    public void testViewGeoLocation(){
        // More of an UITesting to see if this displays on the Borrowed Item in ItemView once implemented.
        Item item = new Item("game", "name");
        item.setBorrowed();
        assertTrue(item.isBorrowed());
        assertTrue("Geolocation is not set by the lender yet.", item.getLocation() != null);
    }
}
