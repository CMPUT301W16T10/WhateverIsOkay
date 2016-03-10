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

    // added the adapter for setting up the list view and passing via intent
    private ArrayAdapter<Bid> adapter;

    private EditText GameName;
    private EditText Players;
    private EditText Age;
    private EditText TimeReq;
    private EditText Platform;
    private Item item;

    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        // turn that mode string back into an integer. there's maybe a way to use an extra that is an integer but i didn't look too closely.
        // TODO: change to passing integer like below
        String mode = getIntent().getStringExtra("mode");
        final int activity_mode = Integer.parseInt(mode);

        //TODO: set item to be the Item object being displayed when view is loaded (could be done on controller side)
        GameName = (EditText) findViewById(R.id.ViewItem_NameEdit);
        Players = (EditText) findViewById(R.id.ViewItem_PlayersEdit);
        Age = (EditText) findViewById(R.id.ViewItem_AgeEdit);
        TimeReq = (EditText) findViewById(R.id.ViewItem_TimeReqEdit);
        Platform = (EditText) findViewById(R.id.ViewItem_PlatformEdit);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(UserController.getCurrentUser().getUsername());

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
        View g = findViewById(R.id.ViewItem_PlaceBid_Text);
        g.setVisibility(View.GONE);
        View h = findViewById(R.id.ViewItem_bidValue);
        h.setVisibility(View.GONE);

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                String name = GameName.getText().toString();
                String players = Players.getText().toString();
                String age = Age.getText().toString();
                String timeReq = TimeReq.getText().toString();
                String platform = Platform.getText().toString();

                Item item = new Item(name, user.getUsername(), players, age, timeReq, platform);

                user.addItem(item); // the information stored in elastic search online is updated inside user class via this method

                // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                Toast.makeText(getApplicationContext(), "Item has been successfully added.", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                Toast.makeText(getApplicationContext(), "Item addition cancelled.", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });
    }

    private void setupEditMode(){
        //hide unused buttons
        View e = findViewById(R.id.ViewItem_Bid);
        e.setVisibility(View.GONE);
        View f = findViewById(R.id.ViewItem_ViewOwner);
        f.setVisibility(View.GONE);
        View g = findViewById(R.id.ViewItem_PlaceBid_Text);
        g.setVisibility(View.GONE);
        View h = findViewById(R.id.ViewItem_bidValue);
        h.setVisibility(View.GONE);

        item = ItemController.getCurrentItem();

        //TODO probably a better way to do this
        //'this' needs to be accessed by AlertDialog.Builder, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        // gets the item info to display on the EditText fields
        GameName.setText(item.getName());
        Players.setText(item.getPlayers());
        Age.setText(item.getAge());
        TimeReq.setText(item.getTimeReq());
        Platform.setText(item.getPlatform());

        adapter = new ArrayAdapter<Bid>(this, R.layout.my_bids_list_view, item.getBids());
        adapter.notifyDataSetChanged();


        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.ViewItem_bidsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bid bid = item.getBids().get(position);
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("What do you wish to do with this bid from " + bid.getBidder() + " for " + bid.getAmount() + "?");
                adBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Accept this bid, decline the rest", Toast.LENGTH_SHORT).show();
                    }
                });
                adBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Decline this bid", Toast.LENGTH_SHORT).show();
                    }
                });
                adBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                item.setName(GameName.getText().toString());
                item.setPlayers(Players.getText().toString());
                item.setAge(Age.getText().toString());
                item.setTimeReq(TimeReq.getText().toString());
                item.setPlatform(Platform.getText().toString());

                ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                updateItem.execute(item);

                // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                Toast.makeText(getApplicationContext(), "Item has been updated", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);

                // TODO investigate why pressing this button results in null pointer exception for user.setitems() in the viewMyItems
                // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                Toast.makeText(getApplicationContext(), "Item edit has been cancelled", Toast.LENGTH_SHORT ).show();
                returnToViewItems();
            }
        });

        Button DeleteButton = (Button) findViewById(R.id.ViewItem_Delete);

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
                        // TODO ensure user's item is not currently borrowed
                        returnToViewItems();
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
        View a = findViewById(R.id.ViewItem_Cancel);
        a.setVisibility(View.GONE);
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

        item = ItemController.getCurrentItem();

        final EditText EnterBid = (EditText) findViewById(R.id.ViewItem_bidValue);

        Button BidButton = (Button) findViewById(R.id.ViewItem_Bid);

        //TODO probably a better way to do this
        //'this' needs to be accessed to call the next intent, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        BidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("Are you sure you want to place a bid of " + EnterBid.getText().toString() + " on this item?");
                adBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double bidAmount = Double.parseDouble(EnterBid.getText().toString());
                        User bidder = UserController.getCurrentUser();
                        Bid bid = new Bid(bidder.getUsername(), bidAmount);
                        item.addBid(bid);

                        //TODO Causes the same error as the update item call from the edit mode
                        ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                        updateItem.execute(item);
                        finish();
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

        Button ViewOwnerButton = (Button) findViewById(R.id.ViewItem_ViewOwner);

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

    public void returnToViewItems() {
        // TODO modify signature to include int mode for when viewItems has multiple modes, thus this function can be called to return it a specific mode.
        Intent intent = new Intent(ViewItem.this, ViewItemsList.class);
        intent.putExtra("mode", ViewItemsList.mode_viewItemsList);
        startActivity(intent);
        finish();
    }

    // TODO add other return to various views where necessary
}
