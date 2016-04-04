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
public class ViewUserProfile extends LocalStorageAwareAppCompatActivity {

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
     * Holds the mode currently used
     */
    private int currentMode;

    /**
     * On create method for setting up view
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final int mode = getIntent().getExtras().getInt("mode");
        currentMode = mode;
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
     * On start method
     */
    @Override
    protected void onStart() {
        super.onStart();
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

                if (isOnline()) {
                    user.setName(name.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setPhone(phone.getText().toString());

                    UserController.UpdateUserProfile updateUserProfile = new UserController.UpdateUserProfile();
                    updateUserProfile.execute(user);

                    Toast.makeText(ViewUserProfile.this, "Updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Connection not found. Feature not available.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
   }

    /**
     * Setup elements only used in view mode (only called from onCreate)
     */
    private void setupViewMode(){

        View v = findViewById(R.id.Update_Profile);
        v.setVisibility(View.GONE);

        username.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

}
