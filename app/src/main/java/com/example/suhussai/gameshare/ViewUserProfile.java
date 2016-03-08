package com.example.suhussai.gameshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

/**
 * Created by sangsoo on 12/02/16.
 */

public class ViewUserProfile extends Activity {

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

        usernameString = getIntent().getStringExtra("username");

        username = (TextView) findViewById(R.id.UsernameText);
        name = (EditText) findViewById(R.id.NameText);
        email = (EditText) findViewById(R.id.EmailText);
        phone = (EditText) findViewById(R.id.PhoneText);

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
            // Newly Created user when logging in
            else if (user == null) {
                username.setText(usernameString);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //TODO: Make edited fields automatically update to server without the update button?
        //TODO: Make passwords editable as well.
        // TODO: Update Button updates changes made to UserProfile (adds a duplicate right now)
        Button updateButton = (Button) findViewById(R.id.Update_Profile);
        updateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                user.setName(name.toString());
                user.setEmail(email.toString());
                user.setPhone(phone.toString());

                UserController.AddUser addUser = new UserController.AddUser();
                addUser.execute(user);

            }
        });

        //TODO: Insert putExtras to make it go to the right View settings.

        // Search for Item
        Button searchForItemsButton = (Button) findViewById(R.id.Search_for_Items);
        searchForItemsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
                finish();
            }
        });

        // View the Items the User has put up to be borrowed
        Button viewMyItemsButton = (Button) findViewById(R.id.View_My_Items);
        viewMyItemsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
                finish();
            }
        });

        // View the Items the User has currently placed bids on.
        Button viewMyBidsPlacedButton = (Button) findViewById(R.id.View_My_Bids_Placed);
        viewMyBidsPlacedButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
                finish();
            }
        });

        // View the Items that the User has currently borrowed from a different User.
        Button currentlyBorrowedItemsButton = (Button) findViewById(R.id.Currently_Borrowed_Items);
        currentlyBorrowedItemsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewUserProfile.this, ViewMyItems.class);
                startActivity(intent);
                finish();
            }
        });

    }
}

