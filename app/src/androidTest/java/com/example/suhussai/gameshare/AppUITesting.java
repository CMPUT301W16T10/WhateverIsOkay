package com.example.suhussai.gameshare;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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
    String name2 = null;
    String username = null;
    String password = null;
    Instrumentation instrumentation;
    Activity activity;
    EditText textInput;
    Instrumentation.ActivityMonitor monitor;
    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testUIComponents() {
        name1 = "Monoply";
        name2 = "Risk";
        username = "sang";
        password = "1";

        // user = new User(username, password);

        item1 = new Item(name1, username);
        item2 = new Item(name2, username);
        item1.setAvailable();
        item2.setAvailable();

        getActivity();

        // login
        onView(withId(R.id.UsernameText))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.PasswordText))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.Login))
                .perform(click());

        // click on view my items
        onView(withId(R.id.View_My_Items))
                .perform(click());

        // US 01.02.01
        // As an owner, I want to view a list of all my things, and their descriptions and statuses.
        onView(withId(R.id.myItemsListView))
                .check(matches(isDisplayed()));
        onView(withId(R.id.myItemsListView))
                .check(matches(isClickable()));

        // US 01.03.01
        // As an owner, I want to view one of my things, its description and status.
        onView(withId(R.id.myItemsListView))
                .perform(click());
        onView(withId(R.id.ViewItem_NameText))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ViewItem_PlayersText))
                .check(matches(isDisplayed()));

        // go back to view items
        pressBack();

        // go back to user profile
        pressBack();

    }

}
