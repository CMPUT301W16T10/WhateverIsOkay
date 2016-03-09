package com.example.suhussai.gameshare;

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
 * Created by dan on 2016-02-21.
 */
public class UserController {
    private static JestDroidClient client;


    // If no client, add a client (from lonelyTwitter)
    public static void verifyConfig(){
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }


    // Add user to cmput301wi16t10/users. (reference: lonelyTwitter)
    public static class AddUser extends AsyncTask<User, Void, Void>{

        @Override
        protected Void doInBackground(User... params){
            verifyConfig();

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
            return null;
        }
    }

    // Get a single user from cmput301wi16t10/users. (reference: lonelyTwitter)
    public static class GetUser extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... params) {
            verifyConfig();

            User user = new User();

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
            return user;
        }
    }

    // Update changes made to a user to cmput301wi16t10/users.
    public static class UpdateUserProfile extends AsyncTask<User, Void, Void>{

        @Override
        protected Void doInBackground(User... params){
            verifyConfig();

            
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
            return null;
        }
    }
}
