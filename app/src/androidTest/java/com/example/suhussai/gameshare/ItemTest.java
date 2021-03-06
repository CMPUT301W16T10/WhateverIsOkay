package com.example.suhussai.gameshare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by suhussai on 11/02/16.
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
        while (user.getItems().contains(item1)) {
            user.deleteItem(item1);
        }
        while (user.getItems().contains(item2)) {
            user.deleteItem(item2);
        }

        user = null;
        item1 = null;
        item2 = null;
    }

    // UC 11
    public void testAddItem() {
        Item item = new Item("new item", "BigOwner");

        int initialGameCount = user.getGameCount();
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount);
        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount + 1);
        assertTrue(user.getItems().contains(item));

        // try adding again.
        user.addItem(item);
        // count should remain the same.
        // i.e request should be ignored.
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount + 1);
        assertTrue(user.getItems().contains(item));


        assertEquals("Item toString incorrect.",
                user.getItem(user.getItems().indexOf(item)).toString(),
                "new item (available) owned by BigOwner");
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
        Item item = new Item("new item", "BigOwner");

        int initialGameCount = user.getGameCount();
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount);
        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount + 1);
        assertTrue(user.getItems().contains(item));

        int itemIndex = user.getItems().indexOf(item);
        user.getItem(itemIndex).setName("new_name");

        // acts as a sleep function
        // because the edit takes time
        while (user.getItem(itemIndex).getName() != "new_name") {}

        assertEquals(user.getItem(itemIndex).getName(), "new_name");

        user.deleteItem(item);
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount + 1);
        assertFalse(user.getItems().contains(item));
    }

    // UC 15
    public void testDeleteItem() {
        Item item = new Item("new item", "BigOwner");

        int initialGameCount = user.getGameCount();
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount);
        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertEquals("Game count incorrect.",
                user.getGameCount(), initialGameCount + 1);
        assertTrue(user.getItems().contains(item));

        user.deleteItem(item);

        // acts as a sleep function
        // because the edit takes time
        while (user.getItems().contains(item) == true) {}
        assertFalse(user.getItems().contains(item));

    }

    // US 02.01.01
    public void testStatus() {
        // test for user story 02.01.01, must be true in all application states for all items
        assertTrue((item2.getStatus() == "available"));
        assertTrue((item1.getStatus() == "available"));

        // getStatus is further tested in many other tests as well
        // Thus this is quite small in order to reduce redundancies
        // in tests.
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

    // UC 42
    public void testSearchAllThings() {
        // This Use Case deals solely with UI interface, not implemented in Unit Tests
    }

    // UC 51
    public void testBid(){
        // test for use case 05.01.01
        User bidder = new User("name","pass");
        Item item = new Item("game","name");
        assertFalse(item.isBidded());
        assertEquals("Item not defaulted to available status.",
                item.getStatus(), "available");

        Bid bid = new Bid(bidder.getUsername(),1.0);
        assertEquals("toString of bid is incorrect",
                bid.toString(), "name bid 1.0");

        item.addBid(bid);
        assertEquals("Item status not changed to bidded status.",
                item.getStatus(), "bidded");
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
        user.addItem(item);
        Bid bid = new Bid("user2",1.0);
        item.addBid(bid);
        //TODO this should not be in the test directly, but does work.
        ArrayList<Item> notifications = new ArrayList<Item>();
        for( Item i : user.getItems()) {
            if (i.hasNewBid()) {
                notifications.add(i);
            }
        }
        user.setNotifications(notifications);
        assertTrue(user.getNotifications().contains(item));
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
        assertEquals("Item not defaulted to available status.",
                item.getStatus(), "available");
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
        assertEquals("Item not defaulted to available status.",
                item.getStatus(), "available");

        Bid bid = new Bid( borrower.getUsername(), amount);
        item.addBid(bid);
        assertEquals("Item status not updated to bidded.",
                item.getStatus(), "bidded");

        assertTrue(item.getBids().contains(bid));
        user.declineBid(bid, item);
        assertFalse(item.getBids().contains(bid));
        assertEquals("Item not reset to available status.",
                item.getStatus(), "available");
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

        user.addItem(item); //user owns borrowed item
        assertEquals("Item not defaulted to available status.",
                item.getStatus(), "available");
        item.setBorrowed(); //item is now borrowed

        assertEquals( item.getStatus(),"borrowed" ); //redundant check

        user.markItemReturned( item );

        assertEquals("Item not reset to available status.",
                item.getStatus(), "available");
    }

    // UC 91
    public void testAddPhoto() {
        Item item = new Item("Risk","user1");
        Bitmap bitmap = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Photo itemPic = new Photo(bitmap);

        assertTrue("This image is null!",itemPic.getImage() != null);
        assertTrue("This item already have an image",item.getImage() == null);
        item.addImage(itemPic);
        assertTrue("The item's picture doesn't match as described",item.getImage() == itemPic.getImage());
    }


    // UC 92
    public void testDeletePhoto() {
        // deleting a photo is part of the UI explicitly.
    }

    // UC 93
    public void testViewPhoto() {
        // viewing a photo is part of the UI explicitly.

    }

    // UC 94
    public void testCheckPhotoSize(){

        Bitmap image = Bitmap.createBitmap(150,150,Bitmap.Config.ARGB_8888);

        double bytes = image.getAllocationByteCount();
        double max_size = 65536; // == 512 kb
        assertTrue("size is less than than 65536 bytes.", bytes >= max_size);
        Photo imagePhoto = new Photo(image);
        Bitmap smallImage = imagePhoto.getImage();
        double newBytes = smallImage.getAllocationByteCount();
        assertTrue("size is greater than than 65536 bytes.", newBytes < max_size);
    }
}
