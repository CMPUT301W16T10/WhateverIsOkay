package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
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


    private Switch filter;
    private String currentKeyword;

    private Spinner platformSpinner;
    private CheckBox platformFilter;
    // initialized such that it passes to itemController
    private String platform = "null";

    private Spinner minAgeSpinner;
    private CheckBox minAgeFilter;
    private String minAge = "0";

    private Spinner minPlayersSpinner;
    private CheckBox minPlayersFilter;
    private String minPlayers = "0";

    private Spinner maxPlayersSpinner;
    private CheckBox maxPlayersFilter;
    private String maxPlayers = "10";

    private Spinner minTimeSpinner;
    private CheckBox minTimeFilter;
    private String minTime = "0";

    public void setPlatform(String platform) {
        this.platform = platform;
    }




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
        minAgeSpinner = (Spinner) findViewById(R.id.minAgeSpinner);
        ArrayAdapter<CharSequence> AgeAdapter = ArrayAdapter.createFromResource(this,
                R.array.age_array, android.R.layout.simple_spinner_item);
        AgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minAgeSpinner.setAdapter(AgeAdapter);

        // Min Players Spinner
        minPlayersSpinner = (Spinner) findViewById(R.id.minPlayersSpinner);
        ArrayAdapter<CharSequence> minPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        minPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minPlayersSpinner.setAdapter(minPlayersAdapter);

        // Max Players Spinner
        maxPlayersSpinner = (Spinner) findViewById(R.id.maxPlayersSpinner);
        ArrayAdapter<CharSequence> maxPlayersAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        maxPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPlayersSpinner.setAdapter(maxPlayersAdapter);

        // set the default filter for max players to the highest number of players in the array
        maxPlayersSpinner.setSelection(maxPlayersSpinner.getAdapter().getCount()-1);

        // Time Required Spinner
        minTimeSpinner = (Spinner) findViewById(R.id.minTimeSpinner);
        ArrayAdapter<CharSequence> timeReqAdapter = ArrayAdapter.createFromResource(this,
                R.array.timeReq_array, android.R.layout.simple_spinner_item);
        timeReqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minTimeSpinner.setAdapter(timeReqAdapter);


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

        filter = (Switch) findViewById(R.id.filterSwitch);
        // initially filter is off
        filterOff();
        // setup the filters
        setupFilter();

        filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // switch is on
                if (isChecked) {
                    filterOn();
                } else {
                    filterOff();
                }
            }
        });
    }


    private void executeAppropriateSearch(String keyword){
        // text is empty, filter is checked off
        if (keyword.isEmpty() && !filter.isChecked()) {
            populateSearchDefault();
        }
        // text is empty, filter is checked on
        else if (keyword.isEmpty() && filter.isChecked()){
            updateItemListUsingKeywords(keyword);
        }
        else if (!keyword.isEmpty()) {
            updateItemListUsingKeywords(keyword);
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

    private void filterOn(){

        currentKeyword = searchView.getQuery().toString();
        filter.setChecked(true);

        platformFilter.setEnabled(true);
        if (platformFilter.isChecked()) {
            platformSpinner.setEnabled(true);
            setPlatform(platformSpinner.getSelectedItem().toString());
        }

        minAgeFilter.setEnabled(true);
        if (minAgeFilter.isChecked()) {
            minAgeSpinner.setEnabled(true);
            minAge = minAgeSpinner.getSelectedItem().toString();
        }

        minPlayersFilter.setEnabled(true);
        if (minPlayersFilter.isChecked()) {
            minPlayersSpinner.setEnabled(true);
            minPlayers = minPlayersSpinner.getSelectedItem().toString();
        }

        maxPlayersFilter.setEnabled(true);
        if (maxPlayersFilter.isChecked()) {
            maxPlayersSpinner.setEnabled(true);
            maxPlayers = maxPlayersSpinner.getSelectedItem().toString();
        }
        minTimeFilter.setEnabled(true);
        if (minTimeFilter.isChecked()) {
            minTimeSpinner.setEnabled(true);
            minTime = minTimeSpinner.getSelectedItem().toString();
        };

        updateItemListUsingKeywords(currentKeyword);

    }

    private void filterOff(){
        currentKeyword = searchView.getQuery().toString();
        filter.setChecked(false);
        setPlatform("null");
        minAge = "0";
        minPlayers = "0";
        maxPlayers = "10";
        minTime = "0";
        findViewById(R.id.platformFilter).setEnabled(false);
        findViewById(R.id.platformSpinner).setEnabled(false);
        findViewById(R.id.minAgeFilter).setEnabled(false);
        findViewById(R.id.minAgeSpinner).setEnabled(false);
        findViewById(R.id.minPlayersFilter).setEnabled(false);
        findViewById(R.id.minPlayersSpinner).setEnabled(false);
        findViewById(R.id.maxPlayersFilter).setEnabled(false);
        findViewById(R.id.maxPlayersSpinner).setEnabled(false);
        findViewById(R.id.minTimeFilter).setEnabled(false);
        findViewById(R.id.minTimeSpinner).setEnabled(false);

        executeAppropriateSearch(currentKeyword);
    }

    private void setupFilter(){
        // whenever the platform is modified, requery with the current filter active and any search keywords
        platformFilter = (CheckBox) findViewById(R.id.platformFilter);
        platformFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchView.getQuery().toString();
                // when platform is checked
                if (platformFilter.isChecked() && filter.isChecked()) {
                    platformSpinner.setEnabled(true);
                    setPlatform(platformSpinner.getSelectedItem().toString());
                    updateItemListUsingKeywords(currentKeyword);
                // when platform is unchecked
                } else {
                    setPlatform("null");
                    platformSpinner.setEnabled(false);
                    executeAppropriateSearch(currentKeyword);
                }
            }
        });

        platformSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (platformFilter.isChecked() && filter.isChecked()) {
                    setPlatform(platformSpinner.getItemAtPosition(position).toString());
                    currentKeyword = searchView.getQuery().toString();
                    updateItemListUsingKeywords(currentKeyword);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
            }
        });


        minAgeFilter = (CheckBox) findViewById(R.id.minAgeFilter);
        minAgeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchView.getQuery().toString();
                // when platform is checked
                if (minAgeFilter.isChecked() && filter.isChecked()) {
                    minAgeSpinner.setEnabled(true);
                    minAge = minAgeSpinner.getSelectedItem().toString();
                    updateItemListUsingKeywords(currentKeyword);
                    // when platform is unchecked
                } else {
                    minAge = "0";
                    minAgeSpinner.setEnabled(false);
                    executeAppropriateSearch(currentKeyword);
                }
            }
        });

        minAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (minAgeFilter.isChecked() && filter.isChecked()) {
                    minAge = minAgeSpinner.getItemAtPosition(position).toString();
                    currentKeyword = searchView.getQuery().toString();
                    updateItemListUsingKeywords(currentKeyword);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
            }
        });


        minPlayersFilter = (CheckBox) findViewById(R.id.minPlayersFilter);
        minPlayersFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchView.getQuery().toString();
                // when platform is checked
                if (minPlayersFilter.isChecked() && filter.isChecked()) {
                    minPlayersSpinner.setEnabled(true);
                    minPlayers = minPlayersSpinner.getSelectedItem().toString();
                    updateItemListUsingKeywords(currentKeyword);
                    // when platform is unchecked
                } else {
                    minPlayers = "0";
                    minPlayersSpinner.setEnabled(false);
                    executeAppropriateSearch(currentKeyword);
                }
            }
        });

        minPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (minPlayersFilter.isChecked() && filter.isChecked()) {
                    minPlayers = minPlayersSpinner.getItemAtPosition(position).toString();
                    currentKeyword = searchView.getQuery().toString();
                    updateItemListUsingKeywords(currentKeyword);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
            }
        });


        maxPlayersFilter = (CheckBox) findViewById(R.id.maxPlayersFilter);
        maxPlayersFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchView.getQuery().toString();
                // when platform is checked
                if (maxPlayersFilter.isChecked() && filter.isChecked()) {
                    maxPlayersSpinner.setEnabled(true);
                    maxPlayers = maxPlayersSpinner.getSelectedItem().toString();
                    updateItemListUsingKeywords(currentKeyword);
                    // when platform is unchecked
                } else {
                    maxPlayers = "10";
                    maxPlayersSpinner.setEnabled(false);
                    executeAppropriateSearch(currentKeyword);
                }
            }
        });

        maxPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (maxPlayersFilter.isChecked() && filter.isChecked()) {
                    maxPlayers = maxPlayersSpinner.getItemAtPosition(position).toString();
                    currentKeyword = searchView.getQuery().toString();
                    updateItemListUsingKeywords(currentKeyword);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
            }
        });


        minTimeFilter = (CheckBox) findViewById(R.id.minTimeFilter);
        minTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchView.getQuery().toString();
                // when platform is checked
                if (minTimeFilter.isChecked() && filter.isChecked()) {
                    minTimeSpinner.setEnabled(true);
                    minTime = minTimeSpinner.getSelectedItem().toString();
                    updateItemListUsingKeywords(currentKeyword);
                    // when platform is unchecked
                } else {
                    minTime = "0";
                    minTimeSpinner.setEnabled(false);
                    executeAppropriateSearch(currentKeyword);
                }
            }
        });

        minTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (minTimeFilter.isChecked() && filter.isChecked()) {
                    minTime = minTimeSpinner.getItemAtPosition(position).toString();
                    currentKeyword = searchView.getQuery().toString();
                    updateItemListUsingKeywords(currentKeyword);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, keep current search filters in place
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
        // 0: mode, 1: keywords, 2: platform, 3: min age, 4: min player, 5: max player, 6: time req
        getItems.execute(ItemController.GetItems.MODE_SEARCH_KEYWORD, keywords, platform, minAge, minPlayers, maxPlayers, minTime);

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

