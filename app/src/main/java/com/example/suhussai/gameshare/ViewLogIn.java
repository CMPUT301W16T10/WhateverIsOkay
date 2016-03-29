package com.example.suhussai.gameshare;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This is the initial view when starting the app. It prompts the user to log in
 */
public class ViewLogIn extends AppCompatActivity {
    /**
     * The user
     */
    private User user;
    /**
     * Field for username
     */
    private EditText userid;
    /**
     * Field for password
     */
    private EditText pass;

    /**
     * On create method
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userid = (EditText) findViewById(R.id.UsernameText);
        pass = (EditText) findViewById(R.id.PasswordText);
        UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                getApplicationContext());

        // Login button logs into the App if the username exists and the password matches OR
        // if the username does not exist.
        Button loginButton = (Button) findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for logging in, will log in if username exists and password is
             * correct, refuse log in if password is wrong, and create new user if a new
             * username is used
             */
            @Override
            public void onClick(View v) {

                String username = userid.getText().toString();
                String password = pass.getText().toString();

                UserController.GetUser getUser = new UserController.GetUser();
                getUser.execute(username);

                //TODO: Need to check for empty passwords and usernames
                //TODO: Need to not allow duplicate username items to exist in server (make user name unique)

                try {
                    user = getUser.get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // user does not exist, create new user.
                // only if connected to the internet
                if (user == null && UserController.isConnected()) {
                    user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    UserController.AddUser addUser = new UserController.AddUser();
                    addUser.execute(user);
                } else if (user != null) {
                    // new user OR user and the password matches -> login.
                    if (user.getUsername().equals(username) &&
                            user.getPassword().equals(password)) {

                        UserController.setCurrentUser(user);
                        setResult(RESULT_OK);

                        if (UserController.isConnected()) {
                            GSController.updateCloud();

                            // Grab the user's items from the controller.
                            ItemController.GetItems getItems = new ItemController.GetItems();
                            getItems.execute(ItemController.GetItems.MODE_GET_MY_ITEMS, user.getUsername());

                            try {
                                user.setItems(getItems.get());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                        }


                        Log.e("TOD", "Updating local storage with new user: " + user.getUsername());
                        UserController.addUserToLocalRecords(user);

                        Intent intent = new Intent(ViewLogIn.this, ViewUserProfile.class);
                        intent.putExtra("mode", ViewUserProfile.MODE_EDIT);
                        UserController.setCurrentUser(user);
                        startActivity(intent);
                        //finish();
                        Toast.makeText(ViewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }

                    // reject login.
                    else {
                        Toast.makeText(ViewLogIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ViewLogIn.this, "Account Not Recognized." +
                            "Need internet connection to register new account.", Toast.LENGTH_SHORT).show();
                    UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                            getApplicationContext());
                }


            }
        });
    }

    @Override
    protected void onStart() {
        UserController.setCurrentUser(null);
        super.onStart();
    }
}