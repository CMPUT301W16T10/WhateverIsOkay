package com.example.bobby.exchangeapp;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class ItemTest extends ActivityInstrumentationTestCase2 {
    public ItemTest() {super(Item.class);}

    public void testAddItems(){
        // test for use case 01.01.01
        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username
        Item item = new Item( name );
        User user = new User( username, "pass" );
        assertFalse( user.getItems().contains( item ) );
        user.addItem(item);
        assertTrue( user.getItems().contains( item ) );
    }

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

    public void testStatus(Item item) {
        // test for use case 02.01.01, must be true in all application states for all items
        assertTrue((item.getStatus() == "available") || (item.getStatus() == "bidded") ||
                (item.getStatus() == "borrowed"));
    }
    
    public void testUpdateProfile(){
        // test for use case 03.02.01
        User user1 = new User("user1","pass");
        ser user2 = new User("user2","pass");
        user1.setUsername(profile2);
        assertEquals(user1, user2);
	}

    
    public void testGetOwnerInfo(){
        // test for use case 03.03.01
        User user = new User("user","pass");
        Item item = new Item("item")
        assertEquals(item.getName(), user.getUsername);
	}

   
    public void testSearchKeyword(){
    	// test case for use case 04.01.01
        User user = new User("user","pass");
        Item item = new Item("item");
        String keyword = “item”;
        assertTrue(item.getName().contains(keyword));
    }

    public void testSearchAllThings(){
        // test for use case 04.02.01
        ArrayList<Item> searchResults = new ArrayList<Item>;
        ArrayList<Item> items = new ArrayList<Item>;
        User user = new User("user","pass");
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        Item item3 = new Item("item3");
        item1.setStatus("available");
        item2.setStatus("bidded");
        item3.setStatus("borrowed");
        items.add(item1);
        items.add(item2);
        items.add(item3);
        for (int i=0; i<items.size(); i++){
            if (items.get(i).getStatus() != “borrowed”)
                searchResults.add(items.get[i]); 
        }
        assertTrue(searchResults.size(), 2);
    }

    public void testBid(){
        // test for use case 05.01.01
        Item item = new Item("item");
        User user = new User("user","pass");
        user.bidOn(item,10);
        assertTrue(item.isBidded());
    }    
    
    public void testSeePendingItems(){
        // test for use case 05.02.01
        ArrayList<Item> pendingItems = new ArrayList<Item>;	
        User user = new User("user","pass");
        Item item1 = new Item("item1");
        item1.setStatus("bidded");
        user.addItem(item1);
        if (item1.getStatus() == “bidded”)
            pendingItems.addThing(thing);
        assertEquals(pendingItems.size(),1);
    }

    public void testNotification(){
        // test for use case 05.03.01
        Item item = new Item("item");
        User user = new User("user","pass");
        User user2 = new User("user2","pass2");
        user.addItem(item);
        user2.bidOn(item,10);
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
        user2.bidOn(item1,10);
        ArrayList<Item> bidded = user.getItems();
        for (int i = 0; i < bidded.size(); i++) {
            if (!bidded.get(i).isBidded()){
                bidded.remove(i);
            }
        }
        assertTrue(bidded.contains(item1));
        assertFalse(bidded.contains(item2));
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

        user2.bidOn(item,10);

        assertTrue(user.getItems().size() == 1);
        assertTrue(user.getItems().get(0) == item);
        assertTrue(user.getItems().get(0).isBidded() == true);

    }

    public void testAcceptOffer(){
        // test for use case 05.06.01
        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username
        String username2 = "Joe.Stevens"; //some other user
        String username3 = "Mike.Gimli"; //third user
        int amount = 2;
        int amount2 = 1;
        Item item = new Item( name );
        User user = new User( username , "pass");
        User borrower = new User( username2, "pass" );
        User borrower2 = new User( username3 , "pass");
        //Bid bid = new Bid( borrower, amount );
        //Bid bid2 = new Bid( borrower2, amount2 );
        Bid bid1 = borrower.bidOn( item, amount );
        Bid bid2 = borrower2.bidOn(item, amount2);
        user.addItem(item);
        assertEquals(item.getStatus(), "Bidded");
        user.acceptBid(bid1);
        assertEquals(item.getStatus(), "Borrowed");
        assertFalse(user.getCurrentBids().contains(bid1)); //ensure the item’s accepted bid is no longer in current bid list
        assertFalse(user.getCurrentBids().contains(bid2)); //ensure the item’s current bids do not include the second, auto declined bid.
        assertEquals(item.getBorrower(), borrower);
        assertEquals(item.getRate(), amount);
    }

    public void testDeclineOffer(){
        // test for use case 05.07.01
        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username
        String username2 = "Joe.Stevens"; //some other user
        int amount = 1;
        Item item = new Item( name );
        User user = new User( username,"pass" );
        User borrower = new User( username2,"pass" );

        Bid bid = borrower.bidOn(item,amount);
        //Item.addBid( bid );
        user.addItem(item);
        assertTrue(user.getCurrentBids().contains(bid));
        user.declineBid(bid);
        assertFalse(user.getCurrentBids().contains(bid));
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

        user2.bidOn(item, 10);
        user.acceptBid(new Bid(user2, 10, item));

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

        user2.bidOn(item, 10);
        user.acceptBid(new Bid(user2, 10, item));

        ArrayList<Item> beingBorrowed = new ArrayList<>();
        beingBorrowed.add(item);
        assertEquals(user.getItemsBeingBorrowed(), beingBorrowed);

    }

    public void testMarkReturned(){
        // test for use case 07.01.01
        String name = "Monopoly"; // some meaningful item name
        String username = "Steve.Smith"; //some meaningful username
        Item item = new Item( name );
        item.setStatus("Borrowed"); //item is now borrowed
        User user = new User( username,"pass" );
        user.addItem(item); //user owns borrowed item
        assertTrue(user.getItemsBeingBorrowed().contains(item));
        assertFalse(user.getAvailableItems().contains(item));
        assertEquals(item.getStatus(), "Borrowed"); //redundant check
        user.markItemReturned(item);
        assertTrue( user.getAvailableItems().contains( item ) );
        assertFalse( user.getItemsBeingBorrowed().contains( item ) );
        assertEquals( item.getStatus(), "Available" ); //redundant check

    }
}
