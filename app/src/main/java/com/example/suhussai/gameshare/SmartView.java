package com.example.suhussai.gameshare;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by bobby on 25/03/16.
 */
public class SmartView extends AppCompatActivity {
    /**
     * On start method
     */
    @Override
    protected void onStart() {
        Log.e("TOD", "Start onStart.");
        UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                getApplicationContext());

        if (UserController.isConnected()) {
            // GSController.updateCloud();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Connection not found. Limited features available.",
                    Toast.LENGTH_SHORT).show();
            // GSController.updateLocalRecords();
        }
        super.onStart();
        Log.e("TOD", "Done onStart.");
    }

    // http://developer.android.com/reference/android/app/Activity.html#onStop%28%29
    // http://stackoverflow.com/questions/18361719/android-activity-ondestroy-is-not-always-called-and-if-called-only-part-of-the
    // Date: Fri Mar 25
    // User: Chris
    // User: ksu

    /**
     * On stop method
     */
    @Override
    protected void onStop() {
        Log.e("TOD", "Start onStop.");
        UserController.setupController((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE),
                getApplicationContext());

        if (UserController.isConnected()) {
            GSController.updateCloud();
        }
        else {
            GSController.updateLocalRecords();
        }

        super.onStop();
        Log.e("TOD", "Done onStop.");
    }


}
