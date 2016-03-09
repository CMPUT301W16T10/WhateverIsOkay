package com.example.suhussai.gameshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by dan on 2016-02-21.
 */
public class ViewItem extends AppCompatActivity{

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

    private String usernameString;
    private User user;

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
        GameName = (EditText) findViewById(R.id.ViewItem_NameEdit);
        Players = (EditText) findViewById(R.id.ViewItem_PlayersEdit);
        Age = (EditText) findViewById(R.id.ViewItem_AgeEdit);
        TimeReq = (EditText) findViewById(R.id.ViewItem_TimeReqEdit);
        Platform = (EditText) findViewById(R.id.ViewItem_PlatformEdit);

        usernameString = getIntent().getStringExtra("username");

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            user = getUser.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
        View v = findViewById(R.id.ViewItem_Delete);
        v.setVisibility(View.GONE);
        View b = findViewById(R.id.ViewItem_Bids_Amount_Text);
        b.setVisibility(View.GONE);
        View c = findViewById(R.id.ViewItem_ExistingBids_Text);
        c.setVisibility(View.GONE);
        View d = findViewById(R.id.ViewItem_bidsListView);
        d.setVisibility(View.GONE);
        View e = findViewById(R.id.ViewItem_Bid);
        e.setVisibility(View.GONE);
        View f = findViewById(R.id.ViewItem_ViewOwner);
        f.setVisibility(View.GONE);

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

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
                    //User user = new User("testuser", "testpass"); // removed as a result of user controller

                    // TODO the controller may need to be involved here.
                    Item item = new Item(name, usernameString, players, age, timeReq, platform);
                    user.addItem(item);

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast.makeText(getApplicationContext(), "Item has been successfully added.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
                finish();
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                Toast.makeText(getApplicationContext(), "Item addition cancelled.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void setupEditMode(){
        //hide unused buttons
        View e = findViewById(R.id.ViewItem_Bid);
        e.setVisibility(View.GONE);
        View f = findViewById(R.id.ViewItem_ViewOwner);
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

        // gets the item info to display on the EditText fields
        GameName.setText(item.getName());
        Players.setText(item.getPlayers());
        Age.setText(item.getAge());
        TimeReq.setText(item.getTimeReq());
        Platform.setText(item.getPlatform());

        // more test data to populate the bid list
        Bid bid1 = new Bid("gameguy",12);
        Bid bid2 = new Bid("poserguy",13);

        item.addBid(bid1);
        item.addBid(bid2);
        bid_list = item.getBids();
        adapter = new ArrayAdapter<Bid>(this, R.layout.my_bids_list_view, bid_list);
        adapter.notifyDataSetChanged();


        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.ViewItem_bidsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO what happens if you click a bid?
                Toast toast = Toast.makeText(getApplicationContext(), "Clicking a bid!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try {
                    item.setName(GameName.getText().toString());
                    item.setPlayers(Players.getText().toString());
                    item.setAge(Age.getText().toString());
                    item.setTimeReq(TimeReq.getText().toString());
                    item.setPlatform(Platform.getText().toString());

                    ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                    updateItem.execute(item);

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item has been updated", Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {

                }
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

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

        Button DeleteButton = (Button) findViewById(R.id.ViewItem_Delete);

        //TODO probably a better way to do this
        //'this' needs to be accessed by AlertDialog.Builder, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("Are you sure you want to delete this item?");
                adBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO modify this so the user is known at this stage. Using a test user in interim.
                        User user = new User("testuser", "testpass");
                        user.deleteItem(item);
                    }
                });
                adBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
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
        View v = findViewById(R.id.ViewItem_Delete);
        v.setVisibility(View.GONE);
        View b = findViewById(R.id.ViewItem_Bids_Amount_Text);
        b.setVisibility(View.GONE);
        View c = findViewById(R.id.ViewItem_ExistingBids_Text);
        c.setVisibility(View.GONE);
        View d = findViewById(R.id.ViewItem_bidsListView);
        d.setVisibility(View.GONE);

        //Make fields uneditable
        GameName.setEnabled(false);
        Players.setEnabled(false);
        Age.setEnabled(false);
        TimeReq.setEnabled(false);
        Platform.setEnabled(false);

        Button BidButton = (Button) findViewById(R.id.ViewItem_Bid);

        BidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO write bid method
            }
        });

        Button ViewOwnerButton = (Button) findViewById(R.id.ViewItem_ViewOwner);

        //TODO probably a better way to do this
        //'this' needs to be accessed to call the next intent, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        ViewOwnerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = item.getOwner();
                Intent intent = new Intent(holder, ViewUserProfile.class);
                intent.putExtra("mode",ViewUserProfile.MODE_VIEW);
                intent.putExtra("username",username);
                //TODO send user to ViewUserProfile (and perhaps mode)
                startActivity(intent);
            }
        });
    }
}
