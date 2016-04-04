package com.example.suhussai.gameshare;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * The view for displaying information about an item. This view has three different modes:
 * 1. adding a new item, 2. editing an existing item, 3. viewing someone else's item
 * @see Item
 */
public class ViewItem extends LocalStorageAwareFragmentActivity implements OnMapReadyCallback {

    // modes are public so others can use them
    /**
     * Code to represent the add new item mode
     */
    public static final int MODE_NEW = 0;
    /**
     * Code to represent the edit existing item mode
     */
    public static final int MODE_EDIT = 1;
    /**
     * Code to represent the view item mode
     */
    public static final int MODE_VIEW = 2;
    /**
     * the adapter for setting up the list view and passing via intent
     */
    private ArrayAdapter<Bid> adapter;
    /**
     * The field displaying the name
     */
    private EditText GameName;
    /**
     * The field displaying the player number
     */
    private EditText Players;
    /**
     * The field displaying the age range
     */
    private EditText Age;
    /**
     * The field displaying the time requirement
     */
    private EditText TimeReq;
    /**
     * The field displaying the platform
     */
    private EditText Platform;
    /**
     * The item being displayed
     */
    private Item item;
    /**
     * The current user
     */
    private User user;
    /**
     * The Image button on the screen, used to capture an image for the game
     */
    private ImageButton pictureButton;
    /**
     * The image that may be associated with an item.
     */
    private Bitmap image = null;
    /**
     * The map that the borrower should meet the lender.
     */
    private GoogleMap map;
    /**
     * Used to facilitate the return from intent
     */
    static final int REQUEST_IMAGE_CAPTURE = 99;
    static final int REQUEST_LOAD_IMG = 98;

    /**
     * The Spinners for the different variables of a game.
     */
    private Spinner platformSpinner, ageSpinner, minPlayersSpinner, maxPlayersSpinner, timeReqSpinner;
    /**
     * The ArrayAdapter for the Spinners.
     */
    private ArrayAdapter<CharSequence> platformAdapter, ageAdapter, minPlayersAdapter, maxPlayersAdapter, timeReqAdapter;

    /**
     * The method to create the view
     * @param savedInstanceState the bundle
     * @see #setupViewMode()
     * @see #setupEditMode()
     * @see #setupNewMode()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        // reference: https://developers.google.com/maps/documentation/android-api/map#add_map_code
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.ViewItem_Map);
        mapFragment.getMapAsync(this);

        // turn that mode string back into an integer. there's maybe a way to use an extra that is an integer but i didn't look too closely.
        // TODO: change to passing integer like below
        String mode = getIntent().getStringExtra("mode");
        int temp;
        if (mode==null){
            temp=getIntent().getExtras().getInt("modeInt");
        }
        else{
            temp=Integer.parseInt(mode);
        }
        final int activity_mode = temp;

        //TODO: set item to be the Item object being displayed when view is loaded (could be done on controller side)
        GameName = (EditText) findViewById(R.id.ViewItem_NameEdit);
        Players = (EditText) findViewById(R.id.ViewItem_PlayersEdit);
        Age = (EditText) findViewById(R.id.ViewItem_AgeEdit);
        TimeReq = (EditText) findViewById(R.id.ViewItem_TimeReqEdit);
        Platform = (EditText) findViewById(R.id.ViewItem_PlatformEdit);
        pictureButton = (ImageButton) findViewById(R.id.ViewItem_pictureButton);

        // From http://developer.android.com/guide/topics/ui/controls/spinner.html
        // Platform Spinner
        platformSpinner = (Spinner) findViewById(R.id.ViewItem_platformSpinner);
        platformAdapter = ArrayAdapter.createFromResource(this,
                R.array.platform_array, android.R.layout.simple_spinner_item);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(platformAdapter);
        // Age Spinner
        ageSpinner = (Spinner) findViewById(R.id.ViewItem_minAgeSpinner);
        ageAdapter = ArrayAdapter.createFromResource(this,
                R.array.age_array, android.R.layout.simple_spinner_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);
        // Min Players Spinner
        minPlayersSpinner = (Spinner) findViewById(R.id.ViewItem_minPlayersSpinner);
        minPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        minPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minPlayersSpinner.setAdapter(minPlayersAdapter);
        // Max Players Spinner
        maxPlayersSpinner = (Spinner) findViewById(R.id.ViewItem_maxPlayersSpinner);
        maxPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        maxPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayersSpinner.setAdapter(maxPlayersAdapter);
        // Time Required Spinner
        timeReqSpinner = (Spinner) findViewById(R.id.ViewItem_minTimeSpinner);
        timeReqAdapter = ArrayAdapter.createFromResource(this,
                R.array.timeReq_array, android.R.layout.simple_spinner_item);
        timeReqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeReqSpinner.setAdapter(timeReqAdapter);

        if (isOnline()) {
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
        }
        else {
            user = UserController.getCurrentUser();
            Toast.makeText(getApplicationContext(),
                    "Connection not found. Feature not available.",
                    Toast.LENGTH_SHORT).show();
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

    @Override
    /**
     * Sets the position on the map to Location set by lender.
     */
    //TODO: Screen rotation crashes this thing. :(
    // Reference: https://developers.google.com/maps/documentation/android-api/map
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        try {
            LatLng latLng = item.getLocation();
            //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            if (item.getLocation() == null) {
                latLng = new LatLng(0, 0);
                map.addMarker(new MarkerOptions().position(latLng).title("DON'T GO HERE"));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            else {
                map.addMarker(new MarkerOptions().position(latLng).title("Location to Meet")
                        .snippet(String.format("lat: %.6f",latLng.latitude)+ String.format(", lng: %.6f",latLng.longitude)));
                // reference: http://stackoverflow.com/questions/29868121/how-do-i-zoom-in-automatically-to-the-current-location-in-google-map-api-for-and
                float zoomLevel = 15; //This goes up to 21
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }


        // Reference: https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMapOptions
        //GoogleMapOptions options = new GoogleMapOptions();
        //options.mapType(map.MAP_TYPE_SATELLITE);
    }

    /**
     * The specific things that must be setup only when in add new item mode
     * Called only from onCreate
     * @see #onCreate(Bundle)
     */
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
        View i = findViewById(R.id.ViewItem_Map);
        i.setVisibility(View.GONE);
        findViewById(R.id.ViewItem_ItemReturned).setVisibility(View.GONE);
        View m = findViewById(R.id.ViewItem_PlayersEdit);
        m.setVisibility(View.GONE);
        View j = findViewById(R.id.ViewItem_AgeEdit);
        j.setVisibility(View.GONE);
        View k = findViewById(R.id.ViewItem_TimeReqEdit);
        k.setVisibility(View.GONE);
        View l = findViewById(R.id.ViewItem_PlatformEdit);
        l.setVisibility(View.GONE);

        pictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (image != null) {
                    viewImage();
                } else {
                    selectImage();
                }
            }
        });

        Button DeleteImageButton = (Button) findViewById(R.id.ViewItem_pictureDeleteButton);

        DeleteImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method to be run when the delete image button is pressed, clears an image from the view.
             * @param v the view
             */
            public void onClick(View v) {
                image = null;
                pictureButton.setImageBitmap(image);
            }
        });

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method to be run when the save button is pressed, creates a new item
             * @param v the view
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                String name = GameName.getText().toString();
                int minPlayers = Integer.parseInt(minPlayersSpinner.getSelectedItem().toString());
                int maxPlayers = Integer.parseInt(maxPlayersSpinner.getSelectedItem().toString());
                int age = Integer.parseInt(ageSpinner.getSelectedItem().toString());
                int timeReq = Integer.parseInt(timeReqSpinner.getSelectedItem().toString());
                String platform = platformSpinner.getSelectedItem().toString();

                Item item = new Item(name, user.getUsername(), minPlayers, maxPlayers, age, timeReq, platform);
                item.addImage(new Photo(image));

                if (isOnline() == false) {
                    item.setId("NO_INTERNET"+user.getGameCount());
                }
                Log.e("TOD", "count before " + user.getItems().size());
                user.addItem(item); // the information stored in elastic search online is updated inside user class via this method
                updateUser(user);
                Log.e("TOD", "count after " + user.getItems().size());
                // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                Toast.makeText(getApplicationContext(), "Item has been successfully added.", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method to be run when the cancel button is pressed, returns to previous view
             * @param v the view
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                Toast.makeText(getApplicationContext(), "Item addition cancelled.", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });
    }

    /**
     * The specific things that must be setup only when in edit item mode
     * Called only from onCreate
     * @see #onCreate(Bundle)
     */
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
        View i = findViewById(R.id.ViewItem_Map);
        i.setVisibility(View.GONE);
        findViewById(R.id.ViewItem_ItemReturned).setVisibility(View.GONE);
        View m = findViewById(R.id.ViewItem_PlayersEdit);
        m.setVisibility(View.GONE);
        View j = findViewById(R.id.ViewItem_AgeEdit);
        j.setVisibility(View.GONE);
        View k = findViewById(R.id.ViewItem_TimeReqEdit);
        k.setVisibility(View.GONE);
        View l = findViewById(R.id.ViewItem_PlatformEdit);
        l.setVisibility(View.GONE);

        if (isOnline() == false) {
            View p = findViewById(R.id.ViewItem_pictureDeleteButton);
            p.setVisibility(View.GONE);
            View i1 = findViewById(R.id.ViewItem_Save);
            i1.setVisibility(View.GONE);
            View i2 = findViewById(R.id.ViewItem_Delete);
            i2.setVisibility(View.GONE);
            View i3 = findViewById(R.id.ViewItem_Cancel);
            i3.setVisibility(View.GONE);

            //Make fields uneditable
            GameName.setEnabled(false);
            Players.setEnabled(false);
            Age.setEnabled(false);
            TimeReq.setEnabled(false);
            Platform.setEnabled(false);
        }

        item = ItemController.getCurrentItem();

        //TODO probably a better way to do this
        //'this' needs to be accessed by AlertDialog.Builder, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        int minPlayerSpinnerPosition = minPlayersAdapter.getPosition(Integer.toString(item.getMinPlayers()));
        int maxPlayerSpinnerPosition = maxPlayersAdapter.getPosition(Integer.toString(item.getMaxPlayers()));
        int ageSpinnerPosition = ageAdapter.getPosition(Integer.toString(item.getAge()));
        int platformSpinnerPosition = platformAdapter.getPosition(item.getPlatform());
        int timeReqSpinnerPosition = timeReqAdapter.getPosition(Integer.toString(item.getTimeReq()));

        // gets the item info to display on the EditText/Spinner fields
        GameName.setText(item.getName());
        minPlayersSpinner.setSelection(minPlayerSpinnerPosition);
        maxPlayersSpinner.setSelection(maxPlayerSpinnerPosition);
        ageSpinner.setSelection(ageSpinnerPosition);
        timeReqSpinner.setSelection(timeReqSpinnerPosition);
        platformSpinner.setSelection(platformSpinnerPosition);
        if( item.hasImage() ) {
            image = item.getImage();
            pictureButton.setImageBitmap(image);
        }

        adapter = new ArrayAdapter<Bid>(this, R.layout.my_bids_list_view, item.getBids());
        adapter.notifyDataSetChanged();


        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.ViewItem_bidsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Method called when pressing on a bid, asks user to accept or decline bid
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Bid bid = item.getBids().get(position);
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("What do you wish to do with this bid from " + bid.getBidder() + " for " + bid.getAmount() + "?");
                adBuilder.setPositiveButton(R.string.dialogAccept, new DialogInterface.OnClickListener() {
                    /**
                     * User accepts bid
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ViewItem.this, ViewMap.class);
                        intent.putExtra("bidString", bid.toString());
                        ItemController.setCurrentItem(item);
                        UserController.setCurrentUser(user);
                        startActivity(intent);
                        finish();
                    }
                });
                adBuilder.setNegativeButton(R.string.dialogDecline, new DialogInterface.OnClickListener() {
                    /**
                     * User declines bid
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.declineBid(bid, item);
                        updateUser(user);
                        Toast.makeText(getApplicationContext(), "Bid declined", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                adBuilder.setNeutralButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
                    /**
                     * User cancels out of decision
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (image != null) {
                    viewImage();
                } else {
                    selectImage();
                }
            }
        });

        Button DeleteImageButton = (Button) findViewById(R.id.ViewItem_pictureDeleteButton);

        DeleteImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The method to be run when the delete image button is pressed, clears an image from the view.
             * @param v the view
             */
            public void onClick(View v) {
                image = null;
                pictureButton.setImageBitmap(image);
            }
        });

        Button SaveButton = (Button) findViewById(R.id.ViewItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method called when pressing save button, any changes will be updated
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                item.setName(GameName.getText().toString());
                item.setMinPlayers(Integer.parseInt(minPlayersSpinner.getSelectedItem().toString()));
                item.setMaxPlayers(Integer.parseInt(maxPlayersSpinner.getSelectedItem().toString()));
                item.setAge(Integer.parseInt(ageSpinner.getSelectedItem().toString()));
                item.setTimeReq(Integer.parseInt(timeReqSpinner.getSelectedItem().toString()));
                item.setPlatform(platformSpinner.getSelectedItem().toString());

                // if image is null, the item's image base 64 is cleared out on save.
                item.addImage(new Photo(image));

                ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                updateItem.execute(item);
                updateUser(user);

                // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                Toast.makeText(getApplicationContext(), "Item has been updated", Toast.LENGTH_SHORT).show();
                returnToViewItems();
            }
        });

        Button CancelButton = (Button) findViewById(R.id.ViewItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method called when pressing cancel button, return to previous view
             */
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
            /**
             * Method called when pressing delete button, will ask for confirmation
             */
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("Are you sure you want to delete this item?");
                adBuilder.setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
                    @Override
                    /**
                     * User confirms, item will be deleted
                     */
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO modify this so the user is known at this stage. Using a test user in interim.
                        //User user = new User("testuser", "testpass");
                        user.deleteItem(item);
                        updateUser(user);
                        // TODO ensure user's item is not currently borrowed
                        returnToViewItems();
                    }
                });

                adBuilder.setNegativeButton(R.string.dialogNo, new DialogInterface.OnClickListener() {
                    @Override
                    /**
                     * User cancels, go back to view item
                     */
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });

        //Set any bids as viewed
        item.setBidsViewed();
        ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
        updateItem.execute(item);
    }

    /**
     * The specific things that must be setup only when in view item mode
     * Called only from onCreate
     * @see #onCreate(Bundle)
     */
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
        View e = findViewById(R.id.ViewItem_pictureDeleteButton);
        e.setVisibility(View.GONE);
        View i = findViewById(R.id.ViewItem_Map);
        i.setVisibility(View.GONE);
        findViewById(R.id.ViewItem_ItemReturned).setVisibility(View.GONE);
        View m = findViewById(R.id.ViewItem_platformSpinner);
        m.setVisibility(View.GONE);
        View j = findViewById(R.id.ViewItem_maxPlayersSpinner);
        j.setVisibility(View.GONE);
        View k = findViewById(R.id.ViewItem_minPlayersSpinner);
        k.setVisibility(View.GONE);
        View l = findViewById(R.id.ViewItem_minAgeSpinner);
        l.setVisibility(View.GONE);
        View n = findViewById(R.id.ViewItem_minTimeSpinner);
        n.setVisibility(View.GONE);
        View o = findViewById(R.id.to);
        o.setVisibility(View.GONE);
        View p = findViewById(R.id.ViewItem_Save);
        p.setVisibility(View.GONE);

        //Make fields uneditable
        GameName.setEnabled(false);
        Players.setEnabled(false);
        Age.setEnabled(false);
        TimeReq.setEnabled(false);
        Platform.setEnabled(false);

        item = ItemController.getCurrentItem();
        image = item.getImage();

        // gets the item info to display on the EditText fields
        GameName.setText(item.getName());
        String playersText = item.getMinPlayers() + " to " + item.getMaxPlayers();
        Players.setText(playersText);
        Age.setText(Integer.toString(item.getAge()));
        TimeReq.setText(Integer.toString(item.getTimeReq()));
        Platform.setText(item.getPlatform());
        if( item.hasImage() ) {
            pictureButton.setImageBitmap(image);
        }

        final EditText EnterBid = (EditText) findViewById(R.id.ViewItem_bidValue);

        EnterBid.requestFocus();

        Button BidButton = (Button) findViewById(R.id.ViewItem_Bid);

        if (item.isBorrowed()){
            View g = findViewById(R.id.ViewItem_PlaceBid_Text);
            g.setVisibility(View.GONE);
            //View h = findViewById(R.id.ViewItem_bidValue);
            EnterBid.setVisibility(View.GONE);
            //View e = findViewById(R.id.ViewItem_Bid);
            BidButton.setVisibility(View.GONE);
            // Map is visibile only if borrowed is true
            i.setVisibility(View.VISIBLE);
            if( user.getUsername().equals(item.getOwner())){
                findViewById(R.id.ViewItem_ItemReturned).setVisibility(View.VISIBLE);
            }
        }

        //TODO probably a better way to do this
        //'this' needs to be accessed to call the next intent, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        BidButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method called when user clicks on bid button, will ask for confirmation
             */
            public void onClick(View v) {
                String bidval = EnterBid.getText().toString();
                // if bid is not numeric
                if (!StringUtils.isNumeric(EnterBid.getText().toString())) {
                    Toast.makeText(ViewItem.this, "Please enter a numeric bid value.", Toast.LENGTH_SHORT).show();
                }

                // if bid is numeric
                else {
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                    adBuilder.setMessage("Are you sure you want to place a bid of " + EnterBid.getText().toString() + " on this item?");
                    adBuilder.setPositiveButton(R.string.dialogYesBid, new DialogInterface.OnClickListener() {
                        /**
                         * User confirms, bid is placed
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            double bidAmount = Double.parseDouble(EnterBid.getText().toString());
                            User bidder = UserController.getCurrentUser();
                            Bid bid = new Bid(bidder.getUsername(), bidAmount);
                            item.addBid(bid);
                            Toast.makeText(ViewItem.this, "Bid Placed.", Toast.LENGTH_SHORT).show();

                            //TODO Causes the same error as the update item call from the edit mode
                            ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                            updateItem.execute(item);
                            finish();
                        }
                    });

                    adBuilder.setNegativeButton(R.string.dialogNoBid, new DialogInterface.OnClickListener() {
                        /**
                         * User cancels, back to view item
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog ad = adBuilder.create();
                    ad.show();
                }
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (item.hasImage()) {
                    viewImage();
                } else {
                    // Nothing. In edit mode, if there's no image then do nothing.
                    Toast.makeText(getApplicationContext(), "This item does not have an image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button ViewOwnerButton = (Button) findViewById(R.id.ViewItem_ViewOwner);

        // Sets the View Borrower button
        final int viewItemsList_mode = getIntent().getExtras().getInt("mode_viewItemsList");
        if (ViewItemsList.MODE_CURRENTLY_LENT_ITEMS == viewItemsList_mode){
            ViewOwnerButton.setText("View Borrower");
        }

        ViewOwnerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method called when View owner button is pressed, user taken to user profile of
             * owner in view mode
             * @see ViewUserProfile
             */
            public void onClick(View v) {
                String username = item.getOwner();
                if (ViewItemsList.MODE_CURRENTLY_LENT_ITEMS == viewItemsList_mode){
                    username = item.getBorrower();
                }
                Intent intent = new Intent(holder, ViewUserProfile.class);
                intent.putExtra("mode", ViewUserProfile.MODE_VIEW);
                intent.putExtra("username", username);
                //TODO send user to ViewUserProfile (and perhaps mode)
                startActivity(intent);
            }
        });

        Button ItemReturned = (Button) findViewById(R.id.ViewItem_ItemReturned);

        ItemReturned.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                item.setAvailable();
                ItemController.UpdateItem updateItem = new ItemController.UpdateItem();
                updateItem.execute(item);
                finish();
                Toast.makeText(getApplicationContext(),"Item has been returned and is now avaialble",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void selectImage() {
        //- See more at: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample#sthash.pAYePD9s.dpuf
        final CharSequence[] options = { "Use Camera", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewItem.this);
        builder.setTitle("Add an image to this item");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Use Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                } else if (options[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult( intent, REQUEST_LOAD_IMG);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void viewImage() {
        Intent intent = new Intent(ViewItem.this, ViewImage.class);
        intent.putExtra("imageToView",image);
        startActivity(intent);
    }

    /**
     * Called to return to ViewItems screen
     */
    public void returnToViewItems() {
        // TODO modify signature to include int mode for when viewItems has multiple modes, thus this function can be called to return it a specific mode.
        Intent intent = new Intent(ViewItem.this, ViewItemsList.class);
        intent.putExtra("mode", ViewItemsList.mode_viewItemsList);
        startActivity(intent);
        finish();
    }

    /**
     * Return from the camera with a new image and add it to the item.
     * @param requestCode if equals image capture, then the activity called was to capture a new image (other requests could exist).
     *                    This parameter simply identifies what intent was originally called to process the return properly
     * @param resultCode determining that cancel wasn't pressed
     * @param data The data coming from the other activity upon its return; the content depends on the requestCode used
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data .getExtras();
            Bitmap tempImage = (Bitmap) extras.get("data");

            Photo itemPhoto = new Photo(tempImage);
            image = itemPhoto.getImage();

            pictureButton.setImageBitmap(image);
        }
        else if(requestCode == REQUEST_LOAD_IMG && resultCode == RESULT_OK) {
            //http://programmerguru.com/android-tutorial/how-to-pick-image-from-gallery/
            // Must grab the intent's data as a URI to extract the image from the gallery
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            Bitmap tempImage = BitmapFactory.decodeFile(imgDecodableString);

            Photo itemPhoto = new Photo(tempImage);
            image = itemPhoto.getImage();

            pictureButton.setImageBitmap(image);
        }
    }

    // TODO add other return to various views where necessary
}
