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

/**
 * Created by bobby on 12/03/16.
 */
public class AppUITesting extends ActivityInstrumentationTestCase2 {
    public AppUITesting() {
        super(ViewLogIn.class);
    }

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
    Instrumentation instrumentation;
    Activity activity;
    EditText textInput;
    Instrumentation.ActivityMonitor monitor;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        name1 = "Monoply";
        name2 = "Risk";
        username = "test_user";
        password = "1";

        // user = new User(username, password);

        item1 = new Item(name1, username);
        item2 = new Item(name2, username);
        item1.setAvailable();
        item2.setAvailable();

        // login
        instrumentation = getInstrumentation();
        activity = getActivity();
        Log.e("CHECK", "activity is " + activity.toString());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textInput = (EditText) activity.findViewById(R.id.UsernameText);
                textInput.setText(username);
                textInput = (EditText) activity.findViewById(R.id.PasswordText);
                textInput.setText(password);
                ((Button) activity.findViewById(R.id.Login)).performClick();
            }
        });

        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(username);

        //TODO: Need to check for empty passwords and usernames
        //TODO: Need to not allow duplicate username items to exist in server (make user name unique)

        try {
            user = getUser.get();

            // user does not exist, create new user.
            if (user == null){
                user = new User();
                user.setUsername(username);
                user.setPassword(password);
                UserController.AddUser addUser = new UserController.AddUser();
                addUser.execute(user);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        user.addItem(item1);
        user.addItem(item2);

    }

    public void testSelectItem() {
        //  Owner selects one thing in their list of things to view its description and status.
        // As an owner, I want to view one of my things, its description and status.
        Intent intent = new Intent(getActivity(), ViewUserProfile.class);
        intent.putExtra("mode", ViewUserProfile.MODE_EDIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getInstrumentation().getContext().startActivity(intent);
//        getInstrumentation().startActivitySync(intent);
        activity = getActivity();
        assertNotNull(activity);
        Log.e("CHECK", "activity is " + activity.toString());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button viewMyItemsButton = (Button) getActivity().findViewById(R.id.View_My_Items);
                viewMyItemsButton.performClick();//click on view my items
            }
        });

        intent = new Intent(getActivity(), ViewItemsList.class);
        intent.putExtra("mode", ViewItemsList.MODE_VIEW_MY_ITEMS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getInstrumentation().getContext().startActivity(intent);
        activity = getActivity(); // view items page
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView LV = (ListView) activity.findViewById(R.id.myItemsListView);

                LV.setSelection(0); // select first item
            }
        });


        intent = new Intent(getActivity(), ViewItem.class);
        String mode = Integer.toString(ViewItem.MODE_EDIT);
        Integer return_mode = ViewItemsList.mode_viewItemsList;
        intent.putExtra("mode", mode);
        intent.putExtra("mode_viewItemsList", return_mode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getInstrumentation().getContext().startActivity(intent);

        activity = getActivity(); // view items page
        assertNotNull(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText nameText = (EditText) activity.findViewById(R.id.NameText);
                assertNotNull(nameText);
                assertTrue(nameText.isShown());
                nameText = (EditText) activity.findViewById(R.id.PhoneText);
                assertNotNull(nameText);
                assertTrue(nameText.isShown());
            }
        });
        assertTrue(true);
    }


    public void testViewItems() {
        setActivityIntent(new Intent());
        ViewItemsList viewItemsList = (ViewItemsList) getActivity();
        String userName = "user1";
        String pass = "pass1";
        User user = null;

        user = User.signIn(userName, pass);

        assertEquals(user.getItems(), new ArrayList());
        assertTrue(viewItemsList.findViewById(R.id.myItemsListView).isShown());

    }

}
