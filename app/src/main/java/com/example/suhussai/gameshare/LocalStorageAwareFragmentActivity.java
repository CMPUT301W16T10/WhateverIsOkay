package com.example.suhussai.gameshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by suhussai on 02/04/16.
 *
 * Has functionalities needed to ensure persistence of data
 * even in offline mode. Provides features to work with
 * offline data storage.
 *
 * Essentially a carbon copy of LocalStorageAwareCompatActivity.
 * The reason the two aren't combined into one class to reduce repetition is
 * because java only allows single class inheritance and we were working with two
 * different kinds of activities: FragmentActivity and AppCompatActivity.
 */
public class LocalStorageAwareFragmentActivity extends FragmentActivity {

    /**
     * The name of the local save file.
     */
    private static String FILENAME = "usersOnThisDevice.txt";

    /*
    http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    User: gar
    Date: Thu Mar 24
    Input: ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // from lonelyTwitter
    // http://www.tutorialspoint.com/java/java_generics.htm
    /**
     * Loads the users from the save file
     * @return list of users
     */
    public ArrayList<User> loadUsersFromFile(){
        ArrayList<User> list;
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();

            // Took from https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html 01-19 2016
            Type listType = new TypeToken<ArrayList<User>>() {}.getType();
            list = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            list = new ArrayList<User>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }

        return list;
    }
    // from lonelyTwitter
    // http://www.tutorialspoint.com/java/java_generics.htm
    /**
     * Saves the users tothe save file
     */
    public void saveToFile(ArrayList<User> list){
        Log.e("TOD", "saving to file these elements: " + list);
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            Gson gson = new Gson();
            gson.toJson(list, out);
            out.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Adds a user to the local save file.
     * @param user the user
     */
    public void addUser(User user){
        ArrayList<User> users = loadUsersFromFile();
        if (userExists(user.getUsername(), user.getPassword()) == false) {
            users.add(user);
            saveToFile(users);
        }
        else {
            Log.e("TOD", "Warning: tried to add user that already existed.");
        }
    }
    /**
     * Deletes a user in the local save file.
     * @param user the user
     */
    public void deleteUser(User user){
        ArrayList<User> users = loadUsersFromFile();
        if (userExists(user.getUsername(), user.getPassword())) {
            users.remove(user);
            saveToFile(users);
        }
        else {
            Log.e("TOD", "Warning: tried to delete user that didn't exist.");
        }
    }
    /**
     * Updates a user in the local save file.
     * @param user the user
     */
    public void updateUser(User user){
        // can do the following because
        // user comparison is done based solely on
        // matching username and password.
        // So while it may look like we are just deleting and
        // adding the user back, it's actually updating
        // because while username and password are the
        // same, other attributes aren't. (like items)
        if (isOnline() == false) {
            user.setUpdatedWhenOffline(true);
        }
        deleteUser(user);
        addUser(user);
    }

    /**
     * Checks if a user already exists in the save file.
     * @param username the user
     * @param password the user's password
     * @return true if already exists
     * @return false if the user does not already exist
     */
    public Boolean userExists(String username, String password){
        ArrayList<User> users = loadUsersFromFile();
        User user = new User(username, password);
        if (users.contains(user)) {
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * Gets the user from the save file.
     * @param username the user
     * @param password the password
     * @return user if the user exists.
     */
    public User getUser(String username, String password){
        ArrayList<User> users = loadUsersFromFile();
        User user = new User(username, password);
        if (userExists(username, password)) {
            return users.get(users.indexOf(user));
        }
        else {
            return null;
        }
    }
}
