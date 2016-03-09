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
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class ViewItemsList extends AppCompatActivity {

    public static final int MODE_SEARCH_FOR_ITEMS = 0;
    public static final int MODE_VIEW_MY_ITEMS = 1;
    public static final int MODE_VIEW_MY_BIDS_PLACED = 2;
    public static final int MODE_CURRENTLY_BORROWED_ITEMS = 3;

    private ArrayAdapter<Item> adapter;

    private String usernameString;
    public static int mode_viewItemsList;

    public String getUsernameString() {
        return usernameString;
    }

    public void setUsernameString() {
        this.usernameString = getIntent().getStringExtra("username");
    }

    public int getMode_viewItemsList() {
        return mode_viewItemsList;
    }

    public void setMode_viewItemsList() {
        this.mode_viewItemsList = getIntent().getExtras().getInt("mode");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        //TODO: Implement search view to do something.

        Button AddNew = (Button) findViewById(R.id.myItemsAddItem);
        AddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_NEW);
                Integer return_mode = getMode_viewItemsList();
                intent.putExtra("mode", mode);
                intent.putExtra("mode_viewItemsList", return_mode);
                intent.putExtra("username", usernameString);
                startActivity(intent);
                finish();
            }
        });

        setUsernameString();
        setMode_viewItemsList();
        System.out.println(Integer.toString(mode_viewItemsList));


        if (mode_viewItemsList == MODE_SEARCH_FOR_ITEMS) {
            setupSearchMode();
        }
        if (mode_viewItemsList == MODE_VIEW_MY_ITEMS) {
            setupViewMyItemsMode();
        }
        if (mode_viewItemsList == MODE_VIEW_MY_BIDS_PLACED) {
            setupViewMyBidsPacedMode();
        }
        if (mode_viewItemsList == MODE_CURRENTLY_BORROWED_ITEMS) {
            setupBorrowedItemsMode();
        }

    }

    //NOTE: some bugs when getting items may still exist due to possibly race conditions.
        // i.e. app crashed when signed into new user and clicked view my items.

    private void setupSearchMode() {
        // TODO: implement another getItems with mode_search_keyword to refine search

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("Search for Items");

        // Hide add button
        View v = findViewById(R.id.myItemsAddItem);
        v.setVisibility(View.GONE);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(getItems.MODE_POPULATE_SEARCH, usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            final User user = getUser.get();
            user.setItems(getItems.get());

            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

            // setting up the list view to have an item click listener
            ListView LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                    Gson gson = new Gson();
                    String fuel_purchase_list = gson.toJson(user.getItems());
                    intent.putExtra("list_as_string", fuel_purchase_list);
                    String purchase_pos = String.valueOf(position);
                    intent.putExtra("position_as_string", purchase_pos);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_VIEW);
                    Integer return_mode = getMode_viewItemsList();
                    intent.putExtra("mode", mode);
                    intent.putExtra("mode_viewItemsList", return_mode);
                    intent.putExtra("username", usernameString);
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


    private void setupViewMyItemsMode() {

        // Hide searchbar:
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(getItems.MODE_GET_MY_ITEMS, usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            final User user = getUser.get();
            user.setItems(getItems.get());

            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

            // setting up the list view to have an item click listener
            ListView LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                    Gson gson = new Gson();
                    String fuel_purchase_list = gson.toJson(user.getItems());
                    intent.putExtra("list_as_string", fuel_purchase_list);
                    String purchase_pos = String.valueOf(position);
                    intent.putExtra("position_as_string", purchase_pos);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_EDIT);
                    Integer return_mode = getMode_viewItemsList();
                    intent.putExtra("mode", mode);
                    intent.putExtra("mode_viewItemsList", return_mode);
                    intent.putExtra("username", usernameString);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void setupViewMyBidsPacedMode() {

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("My Bids Paced");

        // Hide searchbar & add button
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View w = findViewById(R.id.myItemsAddItem);
        w.setVisibility(View.GONE);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(getItems.MODE_GET_BIDDED_ITEMS, usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            final User user = getUser.get();
            user.setItems(getItems.get());

            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

            // setting up the list view to have an item click listener
            ListView LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                    Gson gson = new Gson();
                    String fuel_purchase_list = gson.toJson(user.getItems());
                    intent.putExtra("list_as_string", fuel_purchase_list);
                    String purchase_pos = String.valueOf(position);
                    intent.putExtra("position_as_string", purchase_pos);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_VIEW);
                    Integer return_mode = getMode_viewItemsList();
                    intent.putExtra("mode", mode);
                    intent.putExtra("mode_viewItemsList", return_mode);
                    intent.putExtra("username", usernameString);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void setupBorrowedItemsMode() {

        // Change title:
        TextView title = (TextView) findViewById(R.id.myItemsTextView);
        title.setText("Currently Borrowed Items");

        // Hide searchbar & add button
        View v = findViewById(R.id.myItemsSearchView);
        v.setVisibility(View.GONE);
        View w = findViewById(R.id.myItemsAddItem);
        w.setVisibility(View.GONE);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(getItems.MODE_GET_BORROWED_ITEMS, usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            final User user = getUser.get();
            user.setItems(getItems.get());

            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

            // setting up the list view to have an item click listener
            ListView LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewItemsList.this, ViewItem.class);
                    Gson gson = new Gson();
                    String fuel_purchase_list = gson.toJson(user.getItems());
                    intent.putExtra("list_as_string", fuel_purchase_list);
                    String purchase_pos = String.valueOf(position);
                    intent.putExtra("position_as_string", purchase_pos);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_VIEW);
                    Integer return_mode = getMode_viewItemsList();
                    intent.putExtra("mode", mode);
                    intent.putExtra("mode_viewItemsList", return_mode);
                    intent.putExtra("username", usernameString);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

