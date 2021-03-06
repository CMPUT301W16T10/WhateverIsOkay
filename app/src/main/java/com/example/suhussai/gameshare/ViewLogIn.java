package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This is the initial view when starting the app. It prompts the user to log in
 */
public class ViewLogIn extends LocalStorageAwareAppCompatActivity {
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

        // Login button logs into the App if the username exists and the password matches OR
        // if the username does not exist.
        Button loginButton = (Button) findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for logging in, will log in if username exists and password is
             * correct, refuse log in if password is wrong, and create new user if a new
             * username is used
             */
            @Override
            public void onClick(View v){

                String username = userid.getText().toString().toLowerCase();
                String password = pass.getText().toString().toLowerCase();

                //TODO: Need to check for empty passwords and usernames
                //TODO: Need to not allow duplicate username items to exist in server (make user name unique)

                try {
                    if (isOnline()) {
                        UserController.GetUser getUser = new UserController.GetUser();
                        getUser.execute(username);
                        user = getUser.get();

                        // user does not exist, create new user.
                        if (user == null){
                            Toast.makeText(ViewLogIn.this, "The user does not exist. " +
                                    "Please click Register to create new account.", Toast.LENGTH_SHORT).show();
                        }

                        else if (username.isEmpty() || password.isEmpty()){
                            Toast.makeText(ViewLogIn.this, "Make sure username and password fields are filled in.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // user and the password matches -> login.
                        else if (user.getUsername().equals(username) &&
                                user.getPassword().equals(password) ){

                            User userFromStorage= getUser(username, password);
                            if (userFromStorage != null) {

                                boolean updatedWhenOffline = userFromStorage.getUpdatedWhenOffline();

                                // check if user has been updated when offline
                                if (updatedWhenOffline) {
                                    user = getUser(username, password);
                                    Log.i("TOD", "found items to push up.");
                                    user.setUpdatedWhenOffline(false);
                                    updateUser(user);
                                    // push user and user stuff to cloud
                                    ArrayList<Item> itemArrayList1 = new ArrayList<>();
                                    for (Item item : user.getItems()) {
                                        if (item.getId().contains("NO_INTERNET") || item.getUpdatedWhenOffline()) {
                                            Log.i("TOD", "item ids. " + item.getId());
                                            itemArrayList1.add(item);
                                        }
                                    }

                                    UserController.GetUser getUser1 = new UserController.GetUser();
                                    getUser1.execute(user.getUsername());
                                    user = getUser1.get();

                                    for (Item item : itemArrayList1) {
                                        if (item.getUpdatedWhenOffline()) {
                                            Log.i("TOD", "found item needing to be updated (bidded). " + item.getName());
                                            // update item record in user.getItems()
                                            user.getItems().remove(item);
                                            item.setUpdatedWhenOffline(false);
                                            user.getItems().add(item);

                                            // update local storage
                                            updateUser(user);

                                            // update remote storage
                                            ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                                            updateItem.execute(item);
                                        } else {
                                            user.incrementGameCount();
                                            // set to item's ID before sending to controller
                                            item.setId(user.getUsername() + (char) 31 + user.getGameCount());

                                            // update remote storage
                                            Log.i("TOD", "found item needing to be pushed to cloud. " + item.getName());
                                            ItemController.AddItem addItem = new ItemController.AddItem();
                                            addItem.execute(item);
                                        }

                                        user.getItems().add(item);
                                    }
                                    // update user profile
                                    UserController.UpdateUserProfile updateUserProfile = new UserController.UpdateUserProfile();
                                    updateUserProfile.execute(user);
                                /*
                                http://stackoverflow.com/questions/9863742/how-to-pass-an-arraylist-to-a-varargs-method-parameter
                                User: aioobe
                                Date: Thu Mar 24
                                */
                                }
                            }

                            //UserController.GetUser getUser2 = new UserController.GetUser();
                            //getUser2.execute(username);
                            //user = getUser2.get();
                            updateUser(user);

                            setResult(RESULT_OK);
                            Intent intent = new Intent(ViewLogIn.this, ViewMenu.class);
                            UserController.setCurrentUser(user);
                            startActivity(intent);
                            //finish();
                            Toast.makeText(ViewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }

                        // reject login.
                        else {
                            Toast.makeText(ViewLogIn.this, "Wrong username and password combination.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        // check local storage
                        if (username.isEmpty() || password.isEmpty()){
                            Toast.makeText(ViewLogIn.this, "Make sure username and password fields are filled in.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (userExists(username, password)) {
                            user = getUser(username, password);
                            setResult(RESULT_OK);
                            Intent intent = new Intent(ViewLogIn.this, ViewMenu.class);
                            UserController.setCurrentUser(user);
                            startActivity(intent);
                            //finish();
                            Toast.makeText(ViewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ViewLogIn.this, "Account Not Recognized. " +
                                    "Need internet connection to register new account.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });

        Button registerButton = (Button) findViewById(R.id.Register);
        registerButton.setOnClickListener(new View.OnClickListener(){
            /**
             * On click method for logging in, will log in if username exists and password is
             * correct, refuse log in if password is wrong, and create new user if a new
             * username is used
             */
            @Override
            public void onClick(View v) {

                String username = userid.getText().toString().toLowerCase();
                String password = pass.getText().toString().toLowerCase();

                if (isOnline()) {
                    UserController.GetUser getUser = new UserController.GetUser();
                    getUser.execute(username);
                    try {
                        user = getUser.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (username.isEmpty() || password.isEmpty()){
                        Toast.makeText(ViewLogIn.this, "Make sure username and password fields are filled in.",
                                Toast.LENGTH_SHORT).show();
                    }

                    else if (!StringUtils.isAlphanumeric(username)){
                        Toast.makeText(ViewLogIn.this, "Make sure username only contains alphabets or numbers.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // user does not exist, create new user.-> login.
                    else if (user == null) {
                        user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        UserController.AddUser addUser = new UserController.AddUser();
                        addUser.execute(user);

                        addUser(user);
                        updateUser(user);

                        setResult(RESULT_OK);
                        Intent intent = new Intent(ViewLogIn.this, ViewMenu.class);
                        UserController.setCurrentUser(user);
                        startActivity(intent);
                        //finish();
                        Toast.makeText(ViewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }

                    // user exists therefore reject login.
                    else {
                        Toast.makeText(ViewLogIn.this, "User exists.", Toast.LENGTH_SHORT).show();
                    }

                }

                else {
                    Toast.makeText(ViewLogIn.this,"Need internet connection to register new account.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}