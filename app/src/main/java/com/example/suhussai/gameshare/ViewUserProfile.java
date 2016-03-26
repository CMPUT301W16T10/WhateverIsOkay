package com.example.suhussai.gameshare;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
public class ViewUserProfile extends SmartView {

    // modes are public so others can use them
    /**
     * Mode for viewing own profile
     */
    public static final int MODE_EDIT = 0;
    /**
     * Mode for viewing others' profiles
     */
    public static final int MODE_VIEW = 1;
    /**
     * Username
     */
    private String usernameString;
    /**
     * a new user object
     */
    private User newuser;
    /**
     * The current user
     */
    private User user;
    /**
     * Display of the username
     */
    private TextView username;
    /**
     * Edit field for name
     */
    private EditText name;
    /**
     * Edit field for email
     */
    private EditText email;
    /**
     * Edit field for phone
     */
    private EditText phone;

    /**
     * On start method
     */
    @Override
    protected void onStart() {
        UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                getApplicationContext());

        if (UserController.isConnected()) {
            GSController.updateCloud();
        }
        else {
            GSController.updateLocalRecords();
        }
        super.onStart();
    }



    /**
     * On create method for setting up view
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final int mode = getIntent().getExtras().getInt("mode");

        UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                getApplicationContext());
        user = UserController.getCurrentUser();

        username = (TextView) findViewById(R.id.UsernameText);
        name = (EditText) findViewById(R.id.NameText);
        email = (EditText) findViewById(R.id.EmailText);
        phone = (EditText) findViewById(R.id.PhoneText);

        if (mode==MODE_EDIT){
            username.setText(user.getUsername());
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
            setupEditMode();
        }
        else if (mode==MODE_VIEW){

            usernameString = getIntent().getExtras().getString("username");

            UserController.GetUser getUser = new UserController.GetUser();
            getUser.execute(usernameString);
            try {
                newuser = getUser.get();
                if (newuser==null){
                    finish();//cannot display profile of null user, return
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                finish();//problem getting user, return without displaying profile
            } catch (ExecutionException e) {
                e.printStackTrace();
                finish();//problem getting user, return without displaying profile
            }

            username.setText(newuser.getUsername());
            name.setText(newuser.getName());
            email.setText(newuser.getEmail());
            phone.setText(newuser.getPhone());
            setupViewMode();
        }
    }

    /**
     * Setup elements only used in edit mode (only called from onCreate)
     */
    private void setupEditMode(){
        //TODO: Make passwords editable as well.
        Button updateButton = (Button) findViewById(R.id.Update_Profile);
        updateButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for update button, updates profile information
             */
            @Override
            public void onClick(View v) {

                user.setName(name.getText().toString());
                user.setEmail(email.getText().toString());
                user.setPhone(phone.getText().toString());

                UserController.UpdateUserProfile updateUserProfile = new UserController.UpdateUserProfile();
                updateUserProfile.execute(user);

                Toast.makeText(ViewUserProfile.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO: Insert putExtras to make it go to the right View settings.

        // Search for Item
        Button searchForItemsButton = (Button) findViewById(R.id.Search_for_Items);
        searchForItemsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for search for items button, goes to ViewItemList in search mode
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
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
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
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
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
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
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
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
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
                intent.putExtra("mode", ViewItemsList.MODE_CURRENTLY_LENT_ITEMS);
                startActivity(intent);
            }
        });

        //Check if there are any new bids on the user's items
        //TODO for some reason getItems returns an empty list here, this won't work until that is fixed
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
     * Setup elements only used in view mode (only called from onCreate)
     */
    private void setupViewMode(){


        View v = findViewById(R.id.Search_for_Items);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.newBids);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.View_My_Items);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.View_My_Bids_Placed);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.Currently_Borrowed_Items);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.Update_Profile);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.Currently_Lent_Items);
        v.setVisibility(View.GONE);

        username.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);
    }

}
