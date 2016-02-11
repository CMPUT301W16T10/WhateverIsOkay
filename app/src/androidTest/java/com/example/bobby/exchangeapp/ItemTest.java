package com.example.bobby.exchangeapp;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class ItemTest extends ActivityInstrumentationTestCase2 {
    public ItemTest() {super(Item.class);}

    public void testViewItems(){
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        user = User.signIn(userName, pass);

        assertEquals(user.getItems(), new ArrayList());
        Item item = new Item("s");

        user.addItem(item);
        assertTrue(user.getItems().contains(item));

    }

    public void testBidsOnItems(){
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s");
        user.addItem(item);

        user2.bidOn(item);

        assertTrue(user.getItems().size() == 1);
        assertTrue(user.getItems().get(0) == item);
        assertTrue(user.getItems().get(0).isBidded() == true);

    }

    public void testViewBorrowedItems(){
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s");
        user.addItem(item);

        user2.bidOn(item);
        user.acceptBid(user2);

        ArrayList<Item> borrowed = new ArrayList<>();
        borrowed.add(item);
        assertEquals(user2.getItemsBorrowed(), borrowed);

    }

    public void testViewItemsBeingBorrowed(){
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s");
        user.addItem(item);

        user2.bidOn(item);
        user.acceptBid(user2);

        ArrayList<Item> beingBorrowed = new ArrayList<>();
        beingBorrowed.add(item);
        assertEquals(user.getItemsBeingBorrowed(), beingBorrowed);

    }
}
