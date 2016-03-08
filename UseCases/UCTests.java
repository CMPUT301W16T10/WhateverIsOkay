// UC 21
// Not sure if this is a use case, but test should hold for all things in all application states
public class ThingTest extends ActivityInstrumentationTestCase2{
	public ThingTest(){
        super(MainActivity.class);
    }
	
	public void testStatus(Thing thing){
		assertTrue((thing.status=="available") or (thing.status=="bidded") or (thing.status=="borrowed"));
		// thing.makeBid(1);
		// assertTrue((thing.status=="available") or (thing.status=="bidded") or (thing.status=="borrowed"));
		// thing.setBorrowed();
		// assertTrue((thing.status=="available") or (thing.status=="bidded") or (thing.status=="borrowed"));
		// thing.setReturned();
		// assertTrue((thing.status=="available") or (thing.status=="bidded") or (thing.status=="borrowed"));
	}
}

// UC 51
// Use Case Name			Bid on a thing
// Participating Actors		Borrower
// Goal						Place a bid on a thing which is available
// Trigger					Borrower selects the bid option when viewing a thing
// Precondition				Borrower must currently be viewing a thing
// Postcondition			Borrower's bid is added to the thing, and its status is now 'bidded'
//
// Basic Flow				1.	Borrower clicks the bid option.
//							2.	System displays the minimum bid and opens an edit text field,
//								prompting the user to enter their bid.
//							3.	Borrower enters their bid and clicks a confirm button.
//							4.	System asks for confirmation the borrower wants to place bid
//							5.	Borrower selects yes
//							6.	System adds the bid to the item and marks it as a thing bidded
//								on by the borrower
//
// Exceptions				3.	Borrower enters a non numeric value. System warns borrower and 
//								remains in current state
//							5.	Borrower selects no. System will return to the precondition state
//							6.	Borrower is offline. System notifies borrower that bid did no go
//								through and returns to precondition state
public class BidTest extends ActivityInstrumentationTestCase2{
	public BidTest(){
        super(MainActivity.class);
    }
	
	public void testBid(){
		Thing thing = new Thing();
		User user = new User();
		user.bid(thing,10);//10 is monetary value
		assertTrue(user.biddedThings.contains(thing));
		assertEquals('bidded',thing.status);
	}
}

// UC 53
// Use Case Name			Notify owner of bid
// Participating Actors		None
// Goal						Send notification to owner indicating a bid
// Trigger					Borrower makes a bid on one of the owner's things
// Precondition				Owner has a thing marked as available or bidded
// Postcondition			Notification appears for owner
//
// Basic Flow				1.	Borrower makes a bid on an item
//							2.	System compiles a notifcation containing the borrower's username,
//								item bidded on, and bid amount which is sent to owner.
public class NotificationTest extends ActivityInstrumentationTestCase2{
	public NotificationTest(){
        super(MainActivity.class);
    }
	
	public void testNotification(){
		Thing thing = new Thing();
		thing.setName("Name");
		User user = new User();
		User user2 = new User();
		user.addThing(thing);
		user2.bid(thing,10);//10 is monetary value
		assertTrue(user.notifcations.contains("Bid on Name by "+user2.username+", Amount: 10"));
	}
}

// UC 54
// Use Case Name			View my things with bids
// Participating Actors		Owner
// Goal						View list of all owned items which have a bid placed on them.
// Trigger					Owner selects option to view all current offers
// Precondition				Owner on main screen
// Postcondition			Owner is viewing a list of things and current bids
//
// Basic Flow				1.	Owner clicks the view offers option.
//							2.	System fetches all owner's things which have a bid on them
//								and displays them on screen.
//
// Exceptions				2.	Owner is not connected to internet. System notifies owner and
//								remains on main screen.
public class ViewBidTest extends ActivityInstrumentationTestCase2{
	public ViewBidTest(){
        super(MainActivity.class);
    }
	
	public void testViewBids(){
		Thing thing1 = new Thing();
		Thing thing2 = new Thing();
		User user = new User();
		User user2 = new User();
		user.addThing(thing1);
		user.addThing(thing2);
		user2.bid(thing1,10);//10 is monetary value
		ArrayList<Thing> bidded = user.getBiddedThings();
		assertTrue(bidded.contains(thing1));
		assertFalse(bidded.contains(thing2));
	}
}

// UC 81
// Use Case Name			Make new things offline
// Participating Actors		Owner
// Goal						Add a thing when offline, and automatically push it when online
// Trigger					Owner adds a thing when offline
// Precondition				Owner is offline
// Postcondition			Owner's new thing is pushed
//
// Basic Flow				1.	Owner creates a new thing.
//							2.	System notes that owner is offline and logs change to be pushed
//							3.	Owner goes online.
//							4.	System pushes added things automatically (without prompt).
public class ConnectivityTest extends ActivityInstrumentationTestCase2{
	public ConnectivityTest(){
        super(MainActivity.class);
    }
	
	public void testConnectivityPush(){
		Thing thing = new Thing();
		User user = new User();
		User user2 = new User();
		disableConnection();//some method that simulates user being offline
		user.addThing(thing);
		enableConnection();//undoes the disableConnection method
		ArrayList<Thing> things = user2.searchForThings();
		assertTrue(things.contains(thing));
	}
}
