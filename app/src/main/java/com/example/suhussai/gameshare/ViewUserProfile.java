package com.example.suhussai.gameshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    protected void onStart(){
        super.onStart();

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            user = getUser.get();

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
                System.out.println("boooooooooooo");
                user = new User();
                //user.setUsername(usernameString);
                username.setText(usernameString);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
        searchForItemsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
            }
        });

        // View the Items the User has put up to be borrowed
        Button viewMyItemsButton = (Button) findViewById(R.id.View_My_Items);
        viewMyItemsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                intent.putExtra("username", usernameString);
                startActivity(intent);
            }
        });

        // View the Items the User has currently placed bids on.
        Button viewMyBidsPlacedButton = (Button) findViewById(R.id.View_My_Bids_Placed);
        viewMyBidsPlacedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
            }
        });

        // View the Items that the User has currently borrowed from a different User.
        Button currentlyBorrowedItemsButton = (Button) findViewById(R.id.Currently_Borrowed_Items);
        currentlyBorrowedItemsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
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

