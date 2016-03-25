package com.example.suhussai.gameshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

import static io.searchbox.core.Index.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Controller for actions involving users. Used to search for users, add new users,
 * and update existing user profiles. Uses elastic search.
 * @see User
 */
public class UserController extends GSController{
    /**
     * The user who is currently using the application
     */
    private static User currentUser;

    /**
     * Gets the current user
     * @return the current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user (called at log in)
     * @param currentUser the current user
     */
    public static void setCurrentUser(User currentUser) {
        UserController.currentUser = currentUser;
    }


    // Add user to cmput301wi16t10/users. (reference: lonelyTwitter)
    /**
     * Adds a user to the online database (cmput301wi16t10/users)
     */
    public static class AddUser extends AsyncTask<User, Void, Void>{

        /**
         * Executes the elactic search command
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(User... params){
            if (verifyConfig()) {
                for (User user : params){

                    Index index = new Builder(user).index("cmput301wi16t10").type("users").id(user.getUsername()).build();

                    try {
                        DocumentResult execute = client.execute(index);
                        if (execute.isSucceeded()) {
                        } else {
                            Log.e("TODO", "Our insert of user failed, oh no!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    // Get a single user from cmput301wi16t10/users. (reference: lonelyTwitter)

    /**
     * Gets a specified user object (by username)
     */
    public static class GetUser extends AsyncTask<String, Void, User> {

        /**
         * Executes the elactic search command
         * @param params search parameters
         */
        @Override
        protected User doInBackground(String... params) {

            User user = null;
            if (verifyConfig()) {
                String search_username =
                        "{\n" +
                                "\"query\" : {\n" +
                                "            \"match_all\" : {}\n" +
                                "        },\n" +
                                "        \"filter\" : {\n" +
                                "             \"term\":{\"username\" : \"" + params[0] + "\" }\n" +
                                "        }\n" +
                                "    }\n" +
                                "}\n";

                Search search = new Search.Builder(search_username).addIndex("cmput301wi16t10").addType("users").build();

                try {
                    SearchResult execute = client.execute(search);
                    if (execute.isSucceeded()) {
                        user = execute.getSourceAsObject(User.class);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (getCurrentUser() != null){
                user = getCurrentUser();
            }

            return user;
        }
    }

    // Update changes made to a user to cmput301wi16t10/users.

    /**
     * Updates the database when user has made a change to their profile
     */
    public static class UpdateUserProfile extends AsyncTask<User, Void, Void>{

        /**
         * Executes the elactic search command
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(User... params){
            if (verifyConfig()) {
                for (User user : params){
                    Index update = new Index.Builder(user).index("cmput301wi16t10").type("users").id(user.getUsername()).build();

                    try {
                        JestResult execute = client.execute(update);

                        if (execute.isSucceeded()) {
                            //yay... we don't need this if statement for now
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            return null;
        }
    }

}
