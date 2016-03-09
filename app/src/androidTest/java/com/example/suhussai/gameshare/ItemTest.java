package com.example.suhussai.gameshare;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;

import java.util.ArrayList;

/**
 * Created by bobby on 11/02/16.
 */
public class ItemTest extends ActivityInstrumentationTestCase2 {
    public ItemTest() {super(Item.class);}

    public void testViewItems(){
        setActivityIntent(new Intent());
        ViewItemsList viewItemsList = (ViewItemsList) getActivity();
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        user = User.signIn(userName, pass);

        assertEquals(user.getItems(), new ArrayList());
        assertTrue(viewItemsList.findViewById(R.id.myItemsListView).isShown());

    }

    public void testBidsOnItems(){
        setActivityIntent(new Intent());
        ViewBidsOnItem viewBidsOnItemObj = (ViewBidsOnItem) getActivity();
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s",user);
        user.addItem(item);

        user2.bidOn(item);

        assertTrue(user.getItems().size() == 1);
        assertTrue(user.getItems().get(0) == item);
        assertTrue(user.getItems().get(0).isBidded() == true);
        assertTrue(viewBidsOnItemObj.findViewById(R.id.ViewItem_bidsListView).isShown());

    }

    public void testViewBorrowedItems(){
        setActivityIntent(new Intent());
        ViewBorrowedItems viewBorrowedItems = (ViewBorrowedItems) getActivity();
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s",user);
        user.addItem(item);

        user2.bidOn(item);
        user.acceptBid(user2);

        ArrayList<Item> borrowed = new ArrayList<>();
        borrowed.add(item);
        assertEquals(user2.getBorrowedItems(), borrowed);
        assertTrue(viewBorrowedItems.findViewById(R.id.currentlyBorrowedListView).isShown());
    }

    public void testViewItemsBeingBorrowed(){
        setActivityIntent(new Intent());
        ViewItemsList viewItemsList = (ViewItemsList) getActivity();
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        String userName2 = "user2";
        String pass2 = "pass2";
        User user2 = null;

        user = User.signIn(userName, pass);
        user2 = User.signIn(userName2, pass2);

        Item item = new Item("s",user);
        user.addItem(item);

        user2.bidOn(item);
        user.acceptBid(user2);

        ArrayList<Item> beingBorrowed = new ArrayList<>();
        beingBorrowed.add(item);
        assertEquals(user.getLentItems(), beingBorrowed);
        ViewAsserts.assertOnScreen(getActivity().getWindow().getDecorView(), viewItemsList.findViewById(R.id.myItemsListView));
                //(viewItemsList.findViewById(R.id.myItemsListView).isShown());

    }

    public void testAddItem(){
        String name = "Monoply";
        String username = "user1";
        String password = "pass1";

        User user = new User(username,password);
        Item item = new Item(name,user);

        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertTrue(user.getItems().contains(item));
    }

    public void testSelectItem(){
        String name1 = "Monoply";
        String name2 = "Risk";
        String username = "user1";
        String password = "pass1";

        User user = new User(username, password);

        Item item1 = new Item(name1,user);
        Item item2 = new Item(name2,user);

        user.addItem(item1);
        user.addItem(item2);

        assertEquals(user.getItem(item2).getName(), name2);

    }

    public void testDeleteItem(){
        String name = "Risk";
        String username = "user1";
        String password = "pass1";

        User user = new User(username,password);
        Item item = new Item(name,user);

        user.addItem(item);
        assertTrue(user.getItems().contains(item));
        user.deleteItem(item);
        assertFalse(user.getItems().contains(item));

    }

    public void testEditItem(){
        String name = "Rsk";
        String new_name = "Risk";
        String username = "user1";
        String password = "pass1";

        User user = new User(username,password);
        Item item = new Item(name,user);

        assertEquals(user.getItem(item).getName(), name);
        user.getItem(item).setName(new_name);
        assertEquals(user.getItem(item).getName(), new_name);

    }

    public void testUserProfile(){
        String username = "thinglover";
        String password = "pass1";

        User user = new User(username, password);

        ViewUserProfile userProfile = new ViewUserProfile();

        userProfile.setUserid(user.getUsername());

        assertEquals(userProfile.getUserid(), user.getUsername());



    }


    public void testAcceptOfferOnMyThing(){


        String name = "Monoply"; // some meaningful item name

        String username = "user1"; //some meaningful username

        String username2 = "user2"; //some other user

        String username3 = "user3"; //third user

        double amount = 1.46;

        double amount2 = 1.12;

        User user = new User( username , "");
        Item item = new Item( name ,user);
        User borrower = new User( username2 , "");
        User borrower2 = new User( username3 , "");

        Bid bid = new Bid( borrower, amount );
        Bid bid2 = new Bid( borrower2, amount2 );
        item.addBid( bid );
        item.addBid( bid2 );
        user.addItem(item );
        assertEquals( user.getItem( item ).getStatus(), "Bidded" );
        user.getItem( item ).acceptBid( bid );
        assertEquals( user.getItem( item ).getStatus(), "Borrowed" );
        assertFalse( user.getCurrentBids( item ).contains( bid )); //ensure the item’s accepted bid is no longer in current bid list
        assertFalse( user.getCurrentBids( item ).contains( bid2 )); //ensure the item’s current bids do not include the second, auto declined bid.
        assertEquals( user.getItem( item ).getBorrower(), borrower );
        assertEquals( user.getItem( item ).getRate(), amount );

    }

    public void testDeclineOfferOnMyThing(){


        String name = "Monopoly"; // some meaningful item name

        String username = "Steve.Smith"; //some meaningful username

        String username2 = "Joe.Stevens"; //some other user

        double amount = 1.46;


        User user = new User( username , "");
        Item item = new Item( name ,user);

        User borrower = new User( username2 , "");



        Bid bid = new Bid( borrower, amount );

        item.addBid( bid );

        user.addItem(item );

        assertTrue( user.getCurrentBids( item ).contains( bid ) );

        user.getItem( item ).declineBid( bid );

        assertFalse( user.getCurrentBids( item ).contains( bid ) );



    }

    public void testMarkMyThingReturnedAndAvailable(){


        String name = "Monopoly"; // some meaningful item name

        String username = "Steve.Smith"; //some meaningful username




        User user = new User( username , "");
        Item item = new Item( name,user );
        item.setBorrowed(); //item is now borrowed

        user.addItem(item ); //user owns borrowed item

        assertTrue( user.getOwnedBorrowedItems().contains( item ) );

        assertFalse( user.getOwnedAvailableItems().contains( item ) );

        assertEquals( item.getStatus(),"Borrowed" ); //redundant check

        user.markItemReturned( item );

        assertTrue( user.getOwnedAvailableItems().contains( item ) );

        assertFalse( user.getOwnedBorrowedItems().contains( item ) );

        assertEquals( item.getStatus(), "Available" ); //redundant check

    }


    public void testStatus(Item item) {
        // test for use case 02.01.01, must be true in all application states for all items
        assertTrue((item.getStatus() == "available") || (item.getStatus() == "bidded") ||
                (item.getStatus() == "borrowed"));
    }
    
    public void testUpdateProfile(){
        // test for use case 03.02.01
        User user1 = new User("user1","pass");
        String email = "myemail@gamil.com";

        ViewUserProfile userProfile = new ViewUserProfile();

        userProfile.setUserid(user1.getUsername());
        assertEquals(userProfile.getUseremail(), null);
        userProfile.setUseremail(user1.getEmail());
        assertEquals(userProfile.getUseremail(), user1.getEmail());

	}

    
    public void testGetOwnerInfo(){
        // test for use case 03.03.01
        User user = new User("user","pass");
        Item item = new Item("item",user);
        assertEquals(item.getName(), user.getUsername());
	}

   
    public void testSearchKeyword(){
    	// test case for use case 04.01.01
        User user = new User("user","pass");
        Item item = new Item("item",user);
        String keyword = "item";
        assertTrue(item.getName().contains(keyword));
    }

    public void testSearchAllThings(){
        // test for use case 04.02.01
        ArrayList<Item> searchResults = new ArrayList<Item>();
        ArrayList<Item> items = new ArrayList<Item>();
        User user = new User("user","pass");
        Item item1 = new Item("item1",user);
        Item item2 = new Item("item2",user);
        Item item3 = new Item("item3",user);
        item1.setAvailable();
        item2.setBidded();
        item3.setBorrowed();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        for (int i=0; i<items.size(); i++){
            if (items.get(i).getStatus() != "borrowed")
                searchResults.add(items.get(i));
        }
        assertEquals(searchResults.size(), 2);
    }

    public void testBid(){
        // test for use case 05.01.01
        User user = new User("user","pass");
        Item item = new Item("item",user);
        user.bidOn(item);
        assertTrue(item.isBidded());
    }
    
    public void testSeePendingItems(){
        // test for use case 05.02.01
        ArrayList<Item> pendingItems = new ArrayList<Item>();
        User user = new User("user","pass");
        Item item1 = new Item("item1",user);
        item1.setBidded();
        user.addItem(item1);
        if (item1.getStatus() == "bidded")
            pendingItems.add(item1);
        assertEquals(pendingItems.size(), 1);
    }

    public void testNotification(){
        // test for use case 05.03.01
        User user = new User("user","pass");
        Item item = new Item("item",user);
        User user2 = new User("user2","pass2");
        user.addItem(item);
        user2.bidOn(item);
        assertTrue(user.getNotifications().contains("Bid on item by " + user2.getUsername()));
    }

    public void testViewBids(){
        // test for use case 05.04.01
        User user = new User("user","pass");
        Item item1 = new Item("item1",user);
        Item item2 = new Item("item2",user);
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

    public void testConnectivityPush(){

        User user = new User("user1", "");
        Item item = new Item("Risk",user);
        // disableConnection();//some method that simulates user being offline
        user.addItem(item);
        // enableConnection();//undoes the disableConnection method
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(user.getItem(item));
        assertTrue(items.contains(item));
    }

}
