package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * View for viewing information about user, also the main page when viewing own profile.
 * 2 modes: Edit own profile and view other's profile
 * @see User
 */
public class ViewMenu extends LocalStorageAwareAppCompatActivity {

    /**
     * The current user
     */
    private User user;
    /**
     * On create method for setting up view
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);

        user = UserController.getCurrentUser();

        TextView username = (TextView) findViewById(R.id.Menu);
        username.setText("Hi, " + user.getUsername() + "!");


        // View My UserProfile
        Button viewMyProfilebutton = (Button) findViewById(R.id.View_My_Profile);
        viewMyProfilebutton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for view my profile button, goes to ViewMyProfile in search edit mode.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMenu.this, ViewUserProfile.class);
                intent.putExtra("mode", ViewUserProfile.MODE_EDIT);
                startActivity(intent);
            }
        });

        // Search for Item
        Button searchForItemsButton = (Button) findViewById(R.id.Search_for_Items);
        searchForItemsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for search for items button, goes to ViewItemList in search mode
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMenu.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_SEARCH_FOR_ITEMS);
                startActivity(intent);
            }
        });

        // View the Items the User has put up to be borrowed
        Button viewMyItemsButton = (Button) findViewById(R.id.View_My_Items);
        viewMyItemsButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for search for items button, goes to ViewItemList in my items mode
             */
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewMenu.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_VIEW_MY_ITEMS);
                startActivity(intent);
            }
        });

        // View the Items the User has currently placed bids on.
        Button viewMyBidsPlacedButton = (Button) findViewById(R.id.View_My_Bids_Placed);
        viewMyBidsPlacedButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for search for items button, goes to ViewItemList in bids placed mode
             */
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewMenu.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_VIEW_MY_BIDS_PLACED);
                startActivity(intent);
            }
        });

        // View the Items that the User has currently borrowed from a different User.
        Button currentlyBorrowedItemsButton = (Button) findViewById(R.id.Currently_Borrowed_Items);
        currentlyBorrowedItemsButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for search for items button, goes to ViewItemList in borrowed items mode
             */
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewMenu.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_CURRENTLY_BORROWED_ITEMS);
                startActivity(intent);
            }
        });

        // View the Items that the User has currently lent to a different User.
        Button currentlyLentItemsButton = (Button) findViewById(R.id.Currently_Lent_Items);
        currentlyLentItemsButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for search for items button, goes to ViewItemList in borrowed items mode
             */
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewMenu.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_CURRENTLY_LENT_ITEMS);
                startActivity(intent);
            }
        });

        //Check if there are any new bids on the user's items
        getUserStuff(ItemController.GetItems.MODE_GET_MY_ITEMS);
        ArrayList<Item> items = user.getItems();
        int count = 0;
        for (Item i: items){
            if (i.hasNewBid()){
                count++;
            }
        }

        TextView newBids = (TextView) findViewById(R.id.newBids);
        if (count>0){
            newBids.bringToFront();
            newBids.setText(Integer.toString(count));
        }
        else{
            newBids.setVisibility(View.GONE);
        }
    }

    /**
     * On start method
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * On resume method
     */
    @Override
    protected void onResume() {
        super.onResume();
            //Refresh notifications
            getUserStuff(ItemController.GetItems.MODE_GET_MY_ITEMS);
            ArrayList<Item> items = user.getItems();
            int count = 0;
            for (Item i: items){
                if (i.hasNewBid()){
                    count++;
                }
            }


            TextView newBids = (TextView) findViewById(R.id.newBids);
            if (count>0){
                newBids.bringToFront();
                newBids.setElevation(100);
                newBids.setText(Integer.toString(count));
            }
            else{
                newBids.setVisibility(View.GONE);
            }
        }

    /**
     * Gets information about the current user
     * @param mode the mode
     */
    private void getUserStuff(String mode) {
        String username = user.getUsername();

        // update the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(username);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(mode, username);

        // Fills in the places needed to be filled for the User Profile
        try {
            if(getUser.get() != null) {
                user = getUser.get();}
            user.setItems(getItems.get());
        } catch (InterruptedException |ExecutionException e) {
            e.printStackTrace();
        }

    }

}
