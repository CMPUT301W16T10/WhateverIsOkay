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
import android.widget.TextView;

import com.google.gson.Gson;

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
public class ViewItemsList extends AppCompatActivity {
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

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(ItemController.GetItems.MODE_GET_MY_LENT_ITEMS, user.getUsername());

        // Fills in the places needed to be filled for the User Profile
        try {
            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, getItems.get());

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

        searchView = (SearchView) findViewById(R.id.myItemsSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String queryText) {
                updateItemListUsingKeywords(queryText);
                return true;

            }


            public boolean onQueryTextChange(String newText) {
                updateItemListUsingKeywords(newText);
                return true;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
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

        getUserStuff(ItemController.GetItems.MODE_GET_MY_ITEMS);

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}

