package com.example.bobby.exchangeapp;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class ItemTest extends ActivityInstrumentationTestCase2 {
    public ItemTest() {super(Item.class);}

    public void testViewItems(){
        // test for use case 01.02.01
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
        // test for use case 05.05.01
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
        // test for use case 06.01.01
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
        // test for use case 06.02.01
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

    public void testStatus(Item item) {
        // test for use case 02.01.01, must be true in all application states for all items
        assertTrue((item.status == "available") || (item.status == "bidded") ||
                (item.status == "borrowed"));
    }

    public void testBid(){
        // test for use case 05.01.01
        Item item = new Item("item");
        User user = new User("user","pass");
        user.bidOn(item);
        assertTrue(item.isBidded());
    }

    public void testNotification(){
        // test for use case 05.03.01
        Item item = new Item("item");
        User user = new User("user","pass");
        User user2 = new User("user2","pass2");
        user.addItem(item);
        user2.bidOn(item);
        assertTrue(user.getNotifications().contains("Bid on item by " + user2.getUsername()));
    }

    public void testViewBids(){
        // test for use case 05.04.01
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        User user = new User("user","pass");
        User user2 = new User("user2","pass2");
        user.addItem(item1);
        user.addItem(item2);
        user2.bidOn(item1);
        ArrayList<Item> bidded = user.getItems();
        for (int i = 0; i < bidded.size(); i++) {
            if (!bidded.get(i).isBidded()){
                bidded.remove(i);
            }
        }
        assertTrue(bidded.contains(item1));
        assertFalse(bidded.contains(item2));
    }
}
