package com.example.suhussai.gameshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

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
 * Created by suhussai on 24/03/16.
 */
public class GSController {
    /**
     * The client
     */
    protected static JestDroidClient client = null;
    /**
     * Whether or not we are connected to the internet.
     */
    protected static Boolean connected = false;
    /**
     * Context of the calling view.
     */
    protected static Context currentContext = null;
    private static String FILENAME = "usersOnThisDevice.txt";
    protected static String elasticSearchUrl = "http://gameshare-umtest.rhcloud.com";
    private static String serverUri;

    /**
     * Adds the client if there isn't one already (from lonelyTwitter)
     */
    public static Boolean verifyConfig(){
        if (client == null) {
            serverUri = "";
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
        return connected;
    }

    /*
    http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    User: gar
    Date: Thu Mar 24
    Input: ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    */
    public static void setupController(ConnectivityManager cm, Context context) {
        currentContext = context;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        connected = netInfo != null && netInfo.isConnectedOrConnecting();
        // connected = netInfo != null && netInfo.isConnected();
    }


    /**
     * Gets connectivity status.
     * @return
     */
    public static Boolean isConnected() {
        Log.e("TOD", "connections status: "+ connected);
        return connected;
    }

    // from lonelyTwitter
    // http://www.tutorialspoint.com/java/java_generics.htm
    public static ArrayList<User> loadUsersFromFile(){
        ArrayList<User> list;
        try {
            FileInputStream fis = currentContext.openFileInput(FILENAME);
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
    public static <T> void saveToFile(ArrayList<T> list){
        Log.e("TOD", "saving to file these elements: " + list);
        try {
            FileOutputStream fos = currentContext.openFileOutput(FILENAME,
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

    // from lonelyTwitter
    // http://www.tutorialspoint.com/java/java_generics.htm
    public static <T> void saveToFile(T [] list){
        Log.e("TOD", "saving to file these elements: " + list);
        try {
            FileOutputStream fos = currentContext.openFileOutput(FILENAME,
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
     * Updates cloud when internet connectivity is found.
     */
    public static void updateCloud() {
        // read local file for items to push
        Log.e("TOD", "updating the cloud");
        ArrayList<User> userList = new ArrayList<>();

        userList = loadUsersFromFile();
        User userToUpdate = UserController.getCurrentUser();
        if (userList.contains(userToUpdate)) {
            // get user from the local storage
            userToUpdate = userList.get(userList.indexOf(userToUpdate));

            ArrayList<Item> itemArrayList = new ArrayList<>();
            for (Item item : userToUpdate.getItems()) {
                if (item.getId().equals("")) {
                    userToUpdate.incrementGameCount();
                    // set to item's ID before sending to controller
                    item.setId(userToUpdate.getUsername() + (char) 31 + userToUpdate.getGameCount());
                    Log.e("TOD", "found item needing to be pushed to cloud. " + item.getName());
                }
                Log.e("TOD", "item ids. " + item.getId());
                itemArrayList.add(item);
            }
            userToUpdate.setItems(itemArrayList);
        }

        /*
        http://stackoverflow.com/questions/9863742/how-to-pass-an-arraylist-to-a-varargs-method-parameter
        User: aioobe
        Date: Thu Mar 24
         */

        // delete old items of the userToUpdate
        Log.e("TOD", "deleting the items of user in cloud.");
        ItemController.DeleteItem deleteItem = new ItemController.DeleteItem();
        deleteItem.execute(userToUpdate.getItems().toArray(new Item[userToUpdate.getItems().size()]));

        // add the new items of the userToUpdate
        Log.e("TOD", "adding the items of user in cloud.");
        ItemController.AddItem addItem = new ItemController.AddItem();
        addItem.execute(userToUpdate.getItems().toArray(new Item[userToUpdate.getItems().size()]));

        // update user profile
        UserController.UpdateUserProfile updateUserProfile = new UserController.UpdateUserProfile();
        updateUserProfile.execute(userToUpdate);


        updateLocalRecords();
    }

    /**
     * Updates local storage when internet connectivity is not found.
     */
    public static void updateLocalRecords() {
        // read local file for items to push
        Log.e("TOD", "updating the local storage");
        ArrayList<User> userArrayList = new ArrayList<>();

        userArrayList = loadUsersFromFile();
        User userToUpdate = UserController.getCurrentUser();
        Log.e("TOD", "deleting old object record: " + userArrayList.size());
        userArrayList.remove(userToUpdate);
        Log.e("TOD", "done deleting old object record: " + userArrayList.size());

        userArrayList.add(userToUpdate);
        Log.e("TOD", "saved user had these many items: " + userToUpdate.getItems().size());
        UserController.saveToFile(userArrayList);


    }

    public static void addUserToLocalRecords(User userToUpdate) {
        // read local file for items to push
        Log.e("TOD", "adding new user to local storage");
        ArrayList<User> userArrayList = new ArrayList<>();

        userArrayList = loadUsersFromFile();
        if (userArrayList.contains(userToUpdate)) {
            Log.e("TOD", "it already existed in local storage.");
            return;
        }
        else {
            Log.e("TOD", "new user being added. Count will not decrease.sye");
            updateLocalRecords();
        }

    }
}
