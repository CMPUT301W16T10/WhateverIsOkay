package com.example.suhussai.gameshare;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
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



    /* This no longer exists
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

    } */

    /* This view no longer exists...
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
    }*/

    // UC 11
    public void testAddItem() {
        Item item = new Item("new item", "BigOwner");

        assertFalse(user.getItems().contains(item));
        user.addItem(item);
        assertTrue(user.getItems().contains(item));
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
        name1 = "Monoply";
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

    // UC 14
    public void testEditItem() {
        assertEquals(user.getItems().get(0).getName(), name1);
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
        // test for use case 02.01.01, must be true in all application states for all items
        assertTrue((item2.getStatus() == "available"));
        assertTrue((item1.getStatus() == "available"));
    }


    // UC 31
    public void testUserProfile(){
        // As a user, I want a profile with a unique username and my contact information.
        String username = "thinglover";
        String password = "pass1";
        String password2 = "pass1";

        User user = new User(username, password);
        assertNotNull(user);
        user.setEmail("email@something.com");
        user.setName("Joe");
        user.setPhone("911");

        User user2 = new User(username, password2);
        assertNull(user2); // username must be unique

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
        // As a user, I want to, when a username is presented for a thing, retrieve and show its contact information.
        assertEquals(item1.getOwner(), user.getUsername());
        assertEquals(item2.getOwner(), user.getUsername());
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(item1.getOwner());

        try {
            User testUser = getUser.get();
            assertNotNull(testUser.getEmail());
            assertNotNull(testUser.getPhone());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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

    // UC 94
    // reference: https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
    // http://stackoverflow.com/questions/18507180/get-image-dimension-and-image-size-from-binary
    public void testCheckPhotoSize(){
        // load photo when LoadImageApp.java is implemented
        //Image photo = null;
        //photo = ImageIO.read(new File("image.jpg"));
        File photo = new File("image.jpg");

        double bytes = photo.length(); // 0 b for now since file is null
        double max_size = 65536; // == 512 kb
        assertTrue("size is greater than than 65536 bytes.", bytes < max_size);
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


    public void testAcceptOfferOnMyThing(){
        String name = "Monoply"; // some meaningful item name
        String username = "user1"; //some meaningful username
        String username2 = "user2"; //some other user
        String username3 = "user3"; //third user

        double amount = 1.46;

        double amount2 = 1.12;

        User user = new User( username , "");
        Item item = new Item( name ,username);
        User borrower = new User( username2 , "");
        User borrower2 = new User( username3 , "");

        Bid bid = new Bid( username2, amount );
        Bid bid2 = new Bid( username3, amount2 );
        user.addItem(item);
        item.addBid(bid);
        item.addBid(bid2);
        assertEquals(item.getStatus(), "bidded" );
        user.acceptBid(bid, item);
        assertEquals( item.getStatus(), "borrowed");
        assertFalse(item.getBids().contains(bid)); //ensure the item’s accepted bid is no longer in current bid list
        assertFalse(item.getBids().contains( bid2 )); //ensure the item’s current bids do not include the second, auto declined bid.
        assertEquals(item.getBorrower(), borrower.getUsername() );
        assertEquals(item.getRate(), amount );//Rate is currently not set, so this fails
    }

    public void testDeclineOfferOnMyThing(){
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
        assertFalse(item.getBids().contains( bid ) );
        assertEquals(item.getStatus(),"available");
    }
    /*
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
    */
    public void testNotification(){
        // test for use case 05.03.01
        User user = new User("user","pass");
        Item item = new Item("item","user");
        User user2 = new User("user2","pass2");
        user.addItem(item);
        Bid bid = new Bid("user2",1.0);
        item.addBid(bid);
        user.acceptBid(bid,item);
        //Notifications not implemented yet, so this will fail
        assertTrue(user.getNotifications().contains("Bid on item by " + user2.getUsername()));
    }
    /*
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
*/
}
