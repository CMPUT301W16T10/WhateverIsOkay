package com.example.suhussai.gameshare;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.action.ViewActions;
import android.support.v7.widget.SearchView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
/**
 * Created by bobby on 12/03/16.
 */
public class AppUITesting extends ActivityInstrumentationTestCase2 {
    public AppUITesting() {
        super(ViewLogIn.class);
    }

    private User user = null;
    private Item item1 = null;
    private Item item2 = null;
    String name1 = null;
    String players1 = null;
    String age1 = null;
    String timeReq1 = null;
    String platform1 = null;
    String name2 = null;
    String username = null;
    String password = null;
    String username2 = null;
    String password2 = null;
    Instrumentation instrumentation;
    Activity activity;
    Activity activity2;
    EditText textInput;
    Instrumentation.ActivityMonitor monitor;
    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testUIComponents() {

        // isClickable() from
        // http://stackoverflow.com/questions/32906881/checking-if-a-button-is-clickable-in-espresso-test-android-studio
        // User: Ads
        // Date: Sun-Mar-13

        // test multiple activities using espresso from
        // http://stackoverflow.com/questions/20427411/testing-multiple-activities-with-espresso
        // User: Jigish Chawda
        // Date: Sun-Mar-13

        username = "testui";
        password = "1";

        username2 = "testui2";
        password2 = "2";


        name1 = "Monopoly";
        name2 = "The Game of Risk";
        players1 = "2~4";
        age1 = "10+";
        timeReq1 = "10 hours";
        platform1 = "board";


        // user = new User(username, password);

        item1 = new Item(name1, username);
        item2 = new Item(name2, username);
        item1.setAvailable();
        item2.setAvailable();


        // Start App
        getActivity();
        // Login
        onView(withId(R.id.UsernameText))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());


        // US 01.01.01
        // As an owner, I want to add a thing in my things, each denoted with a clear, suitable description.

        // click on View My Items on ViewUserProfile
        onView(withId(R.id.View_My_Items))
                .perform(click());
        // click on Add Item on ViewItemsList
        onView(withId(R.id.myItemsAddItem))
                .perform(click());
        // fill in the fields on ViewItem
        onView(withId(R.id.ViewItem_NameEdit))
                .perform(typeText(name1), closeSoftKeyboard());
        // click on Save on ViewItem
        onView(withId(R.id.ViewItem_Save))
                .perform(click());
        // After clicking Save, we are back to ViewItemsList
        // Go back to ViewUserProfile
        pressBack();
        // Click View My Items button on ViewUserProfile to see if ViewItemsList has refreshed
        onView(withId(R.id.View_My_Items))
                .perform(click());


        // US 01.02.01
        // As an owner, I want to view a list of all my things, and their descriptions and statuses.

        // Check if the list is displayed
        onView(withId(R.id.myItemsListView))
                .check(matches(isDisplayed()));


        // US 01.03.01
        // As an owner, I want to view one of my things, its description and status.

        // Click the item in list view on ViewItemsList.
        onView(withId(R.id.myItemsListView))
                .perform(click());
        // Check if the correct TextEdit fields are displayed on ViewItem.
        onView(withId(R.id.ViewItem_NameEdit))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_PlayersEdit))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_AgeEdit))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_TimeReqEdit))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_PlatformEdit))
                .check(matches(isDisplayed()));


        // US 01.04.01
        // As an owner, I want to edit a thing in my things.

        // Currently in ViewItem.
        // Edit the text fields on ViewItem
        onView(withId(R.id.ViewItem_NameEdit))
                .perform(replaceText(name2), closeSoftKeyboard());
        onView(withId(R.id.ViewItem_PlayersEdit))
                .perform(typeText(players1), closeSoftKeyboard());
        onView(withId(R.id.ViewItem_AgeEdit))
                .perform(typeText(age1), closeSoftKeyboard());
        onView(withId(R.id.ViewItem_TimeReqEdit))
                .perform(typeText(timeReq1), closeSoftKeyboard());
        onView(withId(R.id.ViewItem_PlatformEdit))
                .perform(typeText(platform1), closeSoftKeyboard());
        // Click Save on ViewItem
        onView(withId(R.id.ViewItem_Save))
                .perform(click());
        // Back on ViewItemsList
        // Go back to ViewUserProfile
        pressBack();
        // Click View My Items button on ViewUserProfile to see if ViewItemsList has refreshed
        onView(withId(R.id.View_My_Items))
                .perform(click());
        // Click the edited item on ViewItemsList to see if it is edited.
        onView(withId(R.id.myItemsListView))
                .perform(click());


        // US 03.01.01
        // As a user, I want a profile with a unique username and my contact information.

        // Go back to ViewItemsList
        // pressBack();
        // Go back to ViewUserProfile
        pressBack();
        // Check if all ViewUserProfile fields are displayed correctly.
        onView(withId(R.id.UserProfile))
                .check(matches(isDisplayed()));
        onView(withId(R.id.NameText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.UsernameText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.EmailText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.PhoneText))
                .check(matches(isDisplayed()));
        // Go back to ViewLogin
        pressBack();
        // Try Logging in with a wrong password. Username is unique so it fails.
        onView(withId(R.id.UsernameText))
                .perform(replaceText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(replaceText("2"), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());
        // Log back in with correct password.
        onView(withId(R.id.UsernameText))
                .perform(replaceText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());


        // US 03.02.01
        // As a user, I want to edit the contact information in my profile.

        // Fill in the Fields for ViewUserProfile.
        onView(withId(R.id.NameText))
                .perform(typeText("UI Tester 1"), closeSoftKeyboard());
        onView(withId(R.id.EmailText))
                .perform(typeText("tester1@uitest.ca"), closeSoftKeyboard());
        onView(withId(R.id.PhoneText))
                .perform(typeText("780-123-4567"), closeSoftKeyboard());
        onView(withId(R.id.Update_Profile))
                .perform(click());
        // Go to ViewLogIn
        pressBack();
        // Login to view if edit has been commited in ViewUserProfile.
        onView(withId(R.id.UsernameText))
                .perform(replaceText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());
        // Clear the Fields for ViewUserProfile.
        onView(withId(R.id.NameText))
                .perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.EmailText))
                .perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.PhoneText))
                .perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.Update_Profile))
                .perform(click());
        // Go back to ViewLogIn
        pressBack();


        // US 04.01.01
        // As a borrower, I want to specify a set of keywords, and search for all things not currently borrowed whose description contains all the keywords.
        // US 04.02.01
        // As a borrower, I want search results to show each thing not currently borrowed with its description, owner username, and status.

        // Login with 2nd username: testui2 // password: 2
        onView(withId(R.id.UsernameText))
                .perform(replaceText(username2), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(replaceText(password2), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());
        //Goto ViewItemsList in Search Mode
        onView(withId(R.id.Search_for_Items))
                .perform(click());
        //Search for The Game of Risk
        //onView(withId(R.id.myItemsSearchView))
        //        .perform(click());
        //onView(withId(android.support.design.R.id.search_src_text))
        //        .perform(typeText(name2), pressKey(66));
        //Select the item.
        onView(withId(R.id.myItemsListView))
                .perform(click());


        // US 03.03.01
        // As a user, I want to, when a username is presented for a thing, retrieve and show its contact information.

        // Click on the ViewOwner button on the ViewItem.
        onView(withId(R.id.ViewItem_ViewOwner))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_ViewOwner))
                .check(matches(isClickable()));
        onView(withId(R.id.ViewItem_ViewOwner))
                .perform(click());
        onView(withId(R.id.UsernameText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.EmailText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.PhoneText))
                .check(matches(isDisplayed()));
        //Go back to the ViewItem.
        pressBack();

        // US 05.01.01
        // As a borrower, I want to bid for an available thing, with a monetary rate (in dollars per hour).

        // Place bid of $10 / hr
        onView(withId(R.id.ViewItem_bidValue))
                .perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.ViewItem_Bid))
                .perform(click());
        onView(withText(R.string.dialogYesBid))
                .perform(click());
        //Automatically goes back to ViewItemsList
        //Go back to ViewUserProfile;
        pressBack();


        // Go back to ViewLogin
        pressBack();
        // Log back in with correct password.
        onView(withId(R.id.UsernameText))
                .perform(replaceText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());

        // US 01.05.01
        // As an owner, I want to delete a thing in my things.

        // Go to My items
        onView(withId(R.id.View_My_Items))
                .perform(click());
        //Select an item
        onView(withId(R.id.myItemsListView))
                .perform(click());
        // Click the delete button.
        onView(withId(R.id.ViewItem_Delete))
                .perform(click());
        onView(withText(R.string.dialogYes))
                .perform(click());
        // Back on ViewItemsList Screen
        // Go back to ViewUserProfile
        pressBack();
        // Click View My Items button on ViewUserProfile to see if ViewItemsList has refreshed
        onView(withId(R.id.View_My_Items))
                .perform(click());


        // US 05.05.01
        // As an owner, I want to view the bids on one of my things.

        // Currently on ViewItemsList, so select an item.
        onView(withId(R.id.myItemsListView))
                .perform(click());
        // Check if bidsLIstView exists in the ViewItem.
        onView(withId(R.id.ViewItem_bidsListView))
                .check(matches(isDisplayed()));
        // go back to ViewItemsList
        pressBack();
        //back to ViewProfile
        pressBack();


        // TODO: add status message in item view activity and enable test below
        // onView(withId(R.id.ViewItem_StatusText))
        //        .check(matches(isDisplayed()));


        // US 06.01.01
        // As a borrower, I want to view a list of things I am borrowing, each thing with its description and owner username.
        // click on currently borrowed items

        onView(withId(R.id.Currently_Borrowed_Items))
                .perform(click());
        onView(withId(R.id.myItemsListView))
                .check(matches(isDisplayed()));
        // go back to user profile
        pressBack();

        // US 06.02.01
        // As an owner, I want to view a list of my things being borrowed, each thing with its description and borrower username.
        onView(withId(R.id.Currently_Lent_Items))
                .perform(click());
        onView(withId(R.id.myItemsListView))
                .check(matches(isDisplayed()));

    }
}