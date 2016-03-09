package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by sangsoo on 12/02/16.
 */

public class ViewUserProfile extends AppCompatActivity {

    // modes are public so others can use them
    public static final int MODE_EDIT = 0;//for viewing own profile
    public static final int MODE_VIEW = 1;//for viewing others' profiles

    private User user;
    private String usernameString;
    private TextView username;
    private EditText name;
    private EditText email;
    private EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final int mode = getIntent().getExtras().getInt("mode");


        usernameString = getIntent().getStringExtra("username");

        username = (TextView) findViewById(R.id.UsernameText);
        name = (EditText) findViewById(R.id.NameText);
        email = (EditText) findViewById(R.id.EmailText);
        phone = (EditText) findViewById(R.id.PhoneText);

        if (mode==MODE_EDIT){
            setupEditMode();
        }
        else if (mode==MODE_VIEW){
            setupViewMode();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /***
         * http://www.tutorialspoint.com/java/util/timer_schedule_period.htm
         final Timer timer = new Timer();
         TimerTask timertask = new TimerTask() {
        @Override
        public void run() {
        System.out.println("TICKING");
        timer.purge();
        }
        };
         timer.schedule(timertask, 1000, 1000);
         ***/

        /*** Fills in the places needed to be filled for the User Profile ***/
        // starts after 1 seconds
        // source: http://stackoverflow.com/questions/3342651/how-can-i-delay-a-java-program-for-a-few-seconds
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        try {
            user = getUser.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // If already an existing user
        if (user != null) {
            username.setText(user.getUsername());
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
        }
        //TODO: fix the problem stated below.
        // Newly Created user when logging in... Need this fix this problem since
        // User was already added when login button was pressed
        else if (user == null) {
            // sets up the new user if the user does not exist.
            // disabling update if user == null
            System.out.println("UPDATE DISABLED");
            View v = findViewById(R.id.Update_Profile);
            v.setVisibility(View.INVISIBLE);

            user = new User();
            //user.setUsername(usernameString);
            username.setText(usernameString);
        }
    }

    private void setupEditMode(){
        //TODO: Make passwords editable as well.
        Button updateButton = (Button) findViewById(R.id.Update_Profile);
        updateButton.setOnClickListener(new View.OnClickListener() {

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
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
                intent.putExtra("username", usernameString);
                intent.putExtra("mode", ViewItemsList.MODE_SEARCH_FOR_ITEMS);
                startActivity(intent);
            }
        });

        // View the Items the User has put up to be borrowed
        Button viewMyItemsButton = (Button) findViewById(R.id.View_My_Items);
        viewMyItemsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
                intent.putExtra("username", usernameString);
                intent.putExtra("mode", ViewItemsList.MODE_VIEW_MY_ITEMS);
                startActivity(intent);
            }
        });

        // View the Items the User has currently placed bids on.
        Button viewMyBidsPlacedButton = (Button) findViewById(R.id.View_My_Bids_Placed);
        viewMyBidsPlacedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
                intent.putExtra("username", usernameString);
                intent.putExtra("mode", ViewItemsList.MODE_VIEW_MY_BIDS_PLACED);
                startActivity(intent);
            }
        });

        // View the Items that the User has currently borrowed from a different User.
        Button currentlyBorrowedItemsButton = (Button) findViewById(R.id.Currently_Borrowed_Items);
        currentlyBorrowedItemsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewItemsList.class);
                intent.putExtra("username", usernameString);
                intent.putExtra("mode", ViewItemsList.MODE_CURRENTLY_BORROWED_ITEMS);
                startActivity(intent);
            }
        });
    }

    private void setupViewMode(){
        //This block may be handled by the onStart method
        /*UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);
        try {
            user = getUser.get();
            if (user==null){
                finish();//cannot display profile of null user, return
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            finish();//problem getting user, return without displaying profile
        } catch (ExecutionException e) {
            e.printStackTrace();
            finish();//problem getting user, return without displaying profile
        }*/

        View v = findViewById(R.id.Search_for_Items);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.View_My_Items);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.View_My_Bids_Placed);
        v.setVisibility(View.GONE);

        v = findViewById(R.id.Currently_Borrowed_Items);
        v.setVisibility(View.GONE);

        username.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);
    }

}
