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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewMyItems extends AppCompatActivity {

    private ArrayAdapter<Item> adapter;

    private String usernameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        usernameString = getIntent().getStringExtra("username");

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(usernameString);

        // Grab the user's items from the controller.
        ItemController.GetItems getItems = new ItemController.GetItems();
        getItems.execute(usernameString);

        // Fills in the places needed to be filled for the User Profile
        try {
            final User user = getUser.get();
            user.setItems(getItems.get());

            /*** test to see if item and id corresponds to each other
            for (int i = 0; i < user.getItems().size() ; i++){
                System.out.println(user.getItems().get(i).getName());
                System.out.println(user.getItems().get(i).getId());
            }***/

            adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, user.getItems());

            // setting up the list view to have an item click listener
            ListView LV = (ListView) findViewById(R.id.myItemsListView);
            LV.setAdapter(adapter);
            LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewMyItems.this, ViewItem.class);
                    Gson gson = new Gson();
                    String fuel_purchase_list = gson.toJson(user.getItems());
                    intent.putExtra("list_as_string", fuel_purchase_list);
                    String purchase_pos = String.valueOf(position);
                    intent.putExtra("position_as_string", purchase_pos);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_EDIT);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                    finish();
                }
            });

            Button AddNew = (Button) findViewById(R.id.myItemsAddItem);
            AddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewMyItems.this, ViewItem.class);
                    // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                    String mode = Integer.toString(ViewItem.MODE_NEW);
                    intent.putExtra("mode", mode);
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

