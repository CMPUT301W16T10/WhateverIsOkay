package com.example.suhussai.gameshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This view displays the results of searching for items, there are four different modes:
 * 1. General search for items (allows keyword search) 2. View items owned by current user\
 * 3. View items current user has bid on 4. View items currently borrowed
 */
/*TODO  The on click method for clicking on search results doesn't have to be defined separately
  TODO  for each mode, as the only difference is the mode ViewItem is called with. This
  TODO  destination mode can be stored in a variable, and the method only defined once.
 */
public class ViewItemsList extends LocalStorageAwareAppCompatActivity {
    /**
     * Mode for general search
     */
    public static final int MODE_SEARCH_FOR_ITEMS = 0;
    /**
     * Mode for owned items
     */
    public static final int MODE_VIEW_MY_ITEMS = 1;
    /**
     * Mode for bidded on items
     */
    public static final int MODE_VIEW_MY_BIDS_PLACED = 2;
    /**
     * Mode for borrowed items
     */
    public static final int MODE_CURRENTLY_BORROWED_ITEMS = 3;
    /**
     * Mode for viewing my items borrowed by another user
     */
    public static final int MODE_CURRENTLY_LENT_ITEMS = 4;
    /**
     * Adapter to hold results
     */
    private ArrayAdapter<Item> adapter;
    /**
     * The search view
     */
    private SearchView searchView;
    /**
     * The list results are displayed in
     */
    private ListView LV;
    /**
     * The current user
     */
    private User user;
    /**
     * The current mode
     */
    public static int mode_viewItemsList;

    private Spinner platformSpinner;

    /**
     * Gets the current mode
     * @return the mdoe
     */
    public int getMode_viewItemsList() {
        return mode_viewItemsList;
    }

    /**
     * sets the current mode (only called from onCreate)
     */
    public void setMode_viewItemsList() {
        this.mode_viewItemsList = getIntent().getExtras().getInt("mode");
    }

    /**
     * On create method, called to start view
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        // call finish when offline and viewing something besides
        // one's own items
        if (isOnline() == false) {
            if (getIntent().getExtras().getInt("mode") == MODE_VIEW_MY_ITEMS) {
                setupViewMyItemsMode();
            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Connection not found. Feature not available.",
                        Toast.LENGTH_SHORT).show();
                // http://stackoverflow.com/questions/10718789/how-to-press-back-button-in-android-programatically
                // User: josephus
                // Mon Mar 28
                finish();

            }
        }

        //TODO: Implement search view to do something.

        Button AddNew = (Button) findViewById(R.id.myItemsAddItem);
        AddNew.setOnClickListener(new View.OnClickListener() {
            /**
             * On click method for the add new button
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_NEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                finish();
            }
        });

        // From http://developer.android.com/guide/topics/ui/controls/spinner.html
        // Platform Spinner
        platformSpinner = (Spinner) findViewById(R.id.platformSpinner);
        ArrayAdapter<CharSequence> platformAdapter = ArrayAdapter.createFromResource(this,
                R.array.platform_array, android.R.layout.simple_spinner_item);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(platformAdapter);
        // Age Spinner
        Spinner ageSpinner = (Spinner) findViewById(R.id.minAgeSpinner);
        ArrayAdapter<CharSequence> AgeAdapter = ArrayAdapter.createFromResource(this,
                R.array.age_array, android.R.layout.simple_spinner_item);
        AgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(AgeAdapter);
        // Min Players Spinner
        Spinner minPlayersSpinner = (Spinner) findViewById(R.id.minPlayersSpinner);
        ArrayAdapter<CharSequence> minPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        minPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minPlayersSpinner.setAdapter(minPlayersAdapter);
        // Max Players Spinner
        Spinner maxPlayersSpinner = (Spinner) findViewById(R.id.maxPlayersSpinner);
        ArrayAdapter<CharSequence> maxPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        maxPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayersSpinner.setAdapter(maxPlayersAdapter);
        // set the default filter for max players to the highest number of players in the array
        maxPlayersSpinner.setSelection(maxPlayersSpinner.getAdapter().getCount()-1);
        // Time Required Spinner
        Spinner timeReqSpinner = (Spinner) findViewById(R.id.minTimeSpinner);
        ArrayAdapter<CharSequence> timeReqAdapter = ArrayAdapter.createFromResource(this,
                R.array.timeReq_array, android.R.layout.simple_spinner_item);
        timeReqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeReqSpinner.setAdapter(timeReqAdapter);


        user = UserController.getCurrentUser();
        setMode_viewItemsList();

        if (mode_viewItemsList == MODE_SEARCH_FOR_ITEMS) {
            setupSearchMode();
        }
        if (mode_viewItemsList == MODE_VIEW_MY_ITEMS) {
            setupViewMyItemsMode();
        }
        if (mode_viewItemsList == MODE_VIEW_MY_BIDS_PLACED) {
            setupViewMyBidsPlacedMode();
        }
        if (mode_viewItemsList == MODE_CURRENTLY_BORROWED_ITEMS) {
            setupBorrowedItemsMode();
        }
        if (mode_viewItemsList == MODE_CURRENTLY_LENT_ITEMS) {
            setupLentItemsMode();
        }

    }

    /**
     * Setup that only happens when user requests to view items that are lent to others.
     */
    private void setupLentItemsMode() {
        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("Currently Lent Items");

        // Hide searchbar & add button
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View w = findViewById(R.id.myItemsAddItem);
        w.setVisibility(View.GONE);
        View x = findViewById(R.id.filterLinearLayout);
        x.setVisibility(View.GONE);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(ItemController.GetItems.MODE_GET_MY_LENT_ITEMS, user.getUsername());

        // Fills in the places needed to be filled for the User Profile
        try {
            final ArrayList<Item> Items = getItems.get();
            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, Items);

            // setting up the list view to have an item click listener
            LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /**
                 * On click method for clicking on items, goes to view item mode
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                    Gson gson = new Gson();
                    Item item = Items.get(position);
                    ItemController.setCurrentItem(item);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_VIEW);
                    Integer return_mode = getMode_viewItemsList();
                    intent.putExtra("mode", mode);
                    intent.putExtra("mode_viewItemsList", return_mode);
                    startActivity(intent);
                    //finish();
                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //NOTE: some bugs when getting items may still exist due to possibly race conditions.
        // i.e. app crashed when signed into new user and clicked view my items.

    /**
     * Setup that only happens for the search mode (Only called from onCreate)
     */
    private void setupSearchMode() {
        // TODO: implement another getItems with mode_search_keyword to refine search

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("Search for Items");

        // Hide add button
        View v = findViewById(R.id.myItemsAddItem);
        v.setVisibility(View.GONE);

        populateSearchDefault();

        // whenever the platform is modified, requery with the current filter active and any search keywords
        platformSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchView = (SearchView) findViewById(R.id.myItemsSearchView);
                String text = searchView.getQuery().toString();
                executeAppropriateSearch(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
            }
        });

        searchView = (SearchView) findViewById(R.id.myItemsSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String queryText) {
                executeAppropriateSearch(queryText);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                executeAppropriateSearch(newText);
                return true;
            }
        });

    }

    private void executeAppropriateSearch(String text){
        if (text.isEmpty()) {
            populateSearchDefault();
        }
        else {
            updateItemListUsingKeywords(text);
        }
    }

    private void populateSearchDefault(){

        getUserStuff(ItemController.GetItems.MODE_POPULATE_SEARCH);

        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

        // setting up the list view to have an item click listener
        LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * On click method for clicking on items, goes to view item mode
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                //TODO: gson is never used
                Gson gson = new Gson();
                Item item = user.getItem(position);
                ItemController.setCurrentItem(item);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_VIEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                //finish();
            }
        });
    }

    /**
     * Updates the displayed items if user starts typing in the search bar
     * @param keywords the entered search terms
     */
    private void updateItemListUsingKeywords(String keywords) {
        // update the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(user.getUsername());

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(ItemController.GetItems.MODE_SEARCH_KEYWORD, keywords);

        // Fills in the places needed to be filled for the User Profile
        try {
            user = getUser.get();
            user.setItems(getItems.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

        // setting up the list view to have an item click listener
        LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * On click method for clicking on items, goes to view item mode
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                Gson gson = new Gson();
                Item item = user.getItem(position);
                ItemController.setCurrentItem(item);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_VIEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                //finish();
            }
        });
    }

    /**
     * Setup that only happens for the view my items mode (Only called from onCreate)
     */
    private void setupViewMyItemsMode() {

        // Hide searchbar:
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View x = findViewById(R.id.filterLinearLayout);
        x.setVisibility(View.GONE);

        getUserStuff(ItemController.GetItems.MODE_GET_MY_ITEMS);

        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());
        adapter.notifyDataSetChanged();

        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * On click method for clicking on items, goes to view item mode
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                Item item = user.getItem(position);
                ItemController.setCurrentItem(item);

                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_EDIT);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Setup that only happens for the view bids placed mode (Only called from onCreate)
     */
    private void setupViewMyBidsPlacedMode() {

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("My Bids Placed");

        // Hide searchbar & add button
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View w = findViewById(R.id.myItemsAddItem);
        w.setVisibility(View.GONE);
        View x = findViewById(R.id.filterLinearLayout);
        x.setVisibility(View.GONE);

        getUserStuff(ItemController.GetItems.MODE_GET_BIDDED_ITEMS);

        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * On click method for clicking on items, goes to view item mode
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                Item item = user.getItem(position);
                ItemController.setCurrentItem(item);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_VIEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                finish();
                }
            });
    }

    /**
     * Setup that only happens for the view borrowed items mode (Only called from onCreate)
     */
    private void setupBorrowedItemsMode() {

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("Currently Borrowed Items");

        // Hide searchbar & add button
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View w = findViewById(R.id.myItemsAddItem);
        w.setVisibility(View.GONE);
        View x = findViewById(R.id.filterLinearLayout);
        x.setVisibility(View.GONE);

        getUserStuff(ItemController.GetItems.MODE_GET_BORROWED_ITEMS);

        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * On click method for clicking on items, goes to view item mode
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                Item item = user.getItem(position);
                ItemController.setCurrentItem(item);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_VIEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Gets information about the current user
     * @param mode the mode
     */
    private void getUserStuff(String mode) {

        if (isOnline()) {
            // update the user from the controller.
            UserController.GetUser getUser = new UserController.GetUser();
            getUser.execute(user.getUsername());

            // Grab the user's items from the controller.
            ItemController.GetItems getItems = new ItemController.GetItems();
            getItems.execute(mode, user.getUsername());

            // Fills in the places needed to be filled for the User Profile
            try {
                user = getUser.get();
                user.setItems(getItems.get());
                if (mode == ItemController.GetItems.MODE_GET_MY_ITEMS) {
                    updateUser(user);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        else if (mode == ItemController.GetItems.MODE_GET_MY_ITEMS) {
            user = UserController.getCurrentUser();
            user = getUser(user.getUsername(), user.getPassword());
        }

    }
}

