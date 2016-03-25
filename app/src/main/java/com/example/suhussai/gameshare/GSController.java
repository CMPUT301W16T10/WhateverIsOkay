package com.example.suhussai.gameshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

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

    /**
     * Adds the client if there isn't one already (from lonelyTwitter)
     */
    public static Boolean verifyConfig(){
        if (client == null) {
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

}
