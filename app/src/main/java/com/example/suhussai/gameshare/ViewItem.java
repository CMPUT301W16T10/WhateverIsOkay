package com.example.suhussai.gameshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by dan on 2016-02-21.
 */
public class ViewItem extends Activity{

    // modes are public so others can use them
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_VIEW = 2;

    // added the adapter and items_list for setting up the list view and passing via intent
    private ArrayAdapter<Bid> adapter;
    private ArrayList<Bid> bid_list;

    private EditText GameName;
    private EditText Players;
    private EditText Age;
    private EditText TimeReq;
    private EditText Platform;
    private Item item;

    // stuff for the GSON
    private ArrayList<Item> items_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        // turn that mode string back into an integer. there's maybe a way to use an extra that is an integer but i didn't look too closely.
        String mode = getIntent().getStringExtra("mode");
        final int activity_mode = Integer.parseInt(mode);

        //TODO: set item to be the Item object being displayed when view is loaded (could be done on controller side)
        GameName = (EditText) findViewById(R.id.AddItem_NameEdit);
        Players = (EditText) findViewById(R.id.AddItem_PlayersEdit);
        Age = (EditText) findViewById(R.id.AddItem_AgeEdit);
        TimeReq = (EditText) findViewById(R.id.AddItem_TimeReqEdit);
        Platform = (EditText) findViewById(R.id.AddItem_PlatformEdit);

        // TODO the magic of "modes"
        if( activity_mode == MODE_NEW ) {
            setupNewMode();
        }
        else if( activity_mode == MODE_EDIT ) {
            setupEditMode();

        }
        else if(activity_mode == MODE_VIEW){
            setupViewMode();
        }
        else {
            // fourth mode maybe?
        }
    }

    private void setupNewMode(){
        // special handling for new entries

        // Hide this button entirely if the mode is MODE_NEW
        View v = findViewById(R.id.AddItem_Delete);
        v.setVisibility(View.GONE);
        View b = findViewById(R.id.ViewItem_Bids_Amount_Text);
        b.setVisibility(View.GONE);
        View c = findViewById(R.id.ViewItem_ExistingBids_Text);
        c.setVisibility(View.GONE);
        View d = findViewById(R.id.bidsListView);
        d.setVisibility(View.GONE);
        View e = findViewById(R.id.AddItem_Bid);
        e.setVisibility(View.GONE);
        View f = findViewById(R.id.AddItem_ViewOwner);
        f.setVisibility(View.GONE);

        Button SaveButton = (Button) findViewById(R.id.AddItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try {

                    String name = GameName.getText().toString();
                    String players = Players.getText().toString();
                    String age = Age.getText().toString();
                    String timeReq = TimeReq.getText().toString();
                    String platform = Platform.getText().toString();

                    //TODO modify this so the user is known at this stage. Using a test user in interim.
                    User user = new User("testuser", "testpass");

                    // TODO the controller may need to be involved here.
                    Item item = new Item(name, user, players, age, timeReq, platform);
                    user.addItem(item);

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item has been successfully added", Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {

                }
            }
        });

        Button CancelButton = (Button) findViewById(R.id.AddItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try{
                    // TODO we may wish to instead go back to the user profile here.
                    GameName.setText("");
                    Players.setText("");
                    Age.setText("");
                    TimeReq.setText("");
                    Platform.setText("");

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item addition has been cancelled", Toast.LENGTH_LONG );
                    toast.show();

                } catch (Exception e) {

                }
            }
        });
    }

    private void setupEditMode(){
        //hide unused buttons
        View e = findViewById(R.id.AddItem_Bid);
        e.setVisibility(View.GONE);
        View f = findViewById(R.id.AddItem_ViewOwner);
        f.setVisibility(View.GONE);

        // special handling for editting entries
        // Receive GSON
        String items_list_string = getIntent().getStringExtra("list_as_string");
        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<Item>>() {}.getType();

        items_list = gson.fromJson(items_list_string, listType);
        String purchase_pos = getIntent().getStringExtra("position_as_string");
        final int pos = Integer.parseInt(purchase_pos);

        item = items_list.get(pos);

        // more test data to populate the bid list
        User user1 = new User("gameguy","strongpassword14x");
        User user2 = new User("poserguy","wkpswd");
        Bid bid1 = new Bid(user1,12);
        Bid bid2 = new Bid(user2,13);

        item.addBid(bid1);
        item.addBid(bid2);
        bid_list = item.getBids();
        adapter = new ArrayAdapter<Bid>(this, R.layout.my_bids_list_view, bid_list);
        adapter.notifyDataSetChanged();


        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.bidsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO what happens if you click a bid?
                Toast toast = Toast.makeText(getApplicationContext(), "Clicking a bid!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        Button SaveButton = (Button) findViewById(R.id.AddItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try {

                    String name = GameName.getText().toString();
                    String players = Players.getText().toString();
                    String age = Age.getText().toString();
                    String timeReq = TimeReq.getText().toString();
                    String platform = Platform.getText().toString();

                    //TODO modify this so the user is known at this stage. Using a test user in interim.
//                    User user = new User("testuser", "testpass");
                    User user = new User("gameguy","goodpassword");

                    // TODO the controller may need to be involved here.
                    Item item = new Item(name, user, players, age, timeReq, platform);
                    user.addItem(item);

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item has been successfully added", Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {

                }
            }
        });

        Button CancelButton = (Button) findViewById(R.id.AddItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try{
                    // TODO change this to go back to the initial data instead of blanking it
                    GameName.setText("");
                    Players.setText("");
                    Age.setText("");
                    TimeReq.setText("");
                    Platform.setText("");

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item addition has been cancelled", Toast.LENGTH_LONG );
                    toast.show();

                } catch (Exception e) {

                }
            }
        });

        Button DeleteButton = (Button) findViewById(R.id.AddItem_Delete);

        //TODO probably a better way to do this
        //'this' needs to be accessed by AlertDialog.Builder, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("Are you sure you want to delete this item?");
                adBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO modify this so the user is known at this stage. Using a test user in interim.
                        User user = new User("testuser", "testpass");
                        user.deleteItem(item);
                    }
                });
                adBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });
    }

    private void setupViewMode(){
        //Hide buttons inactive in this mode
        View v = findViewById(R.id.AddItem_Delete);
        v.setVisibility(View.GONE);
        View b = findViewById(R.id.ViewItem_Bids_Amount_Text);
        b.setVisibility(View.GONE);
        View c = findViewById(R.id.ViewItem_ExistingBids_Text);
        c.setVisibility(View.GONE);
        View d = findViewById(R.id.bidsListView);
        d.setVisibility(View.GONE);

        //Make fields uneditable
        GameName.setEnabled(false);
        Players.setEnabled(false);
        Age.setEnabled(false);
        TimeReq.setEnabled(false);
        Platform.setEnabled(false);

        Button BidButton = (Button) findViewById(R.id.AddItem_Bid);

        BidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO write bid method
            }
        });

        Button ViewOwnerButton = (Button) findViewById(R.id.AddItem_ViewOwner);

        //TODO probably a better way to do this
        //'this' needs to be accessed to call the next intent, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        ViewOwnerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User user = item.getOwner();
                Intent intent = new Intent(holder, ViewUserProfile.class);
                //TODO send user to ViewUserProfile (and perhaps mode)
                startActivity(intent);
            }
        });
    }
}
