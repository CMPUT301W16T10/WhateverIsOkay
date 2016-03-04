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

public class ViewMyItems extends AppCompatActivity {

    // added the adapter and items_list for setting up the list view and passing via intent
    private ArrayAdapter<Item> adapter;
    private ArrayList<Item> items_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        // TODO this is just an example but needs to be replaced with real items.
        items_list = new ArrayList<Item>();
        User user = new User("gameguy","goodpassword");
        Item item1 = new Item("Monopoly",user,"1-2","10-90","90min","board");
        Item item2 = new Item("Cribbage",user,"2-4","15-99","120min","card");
        items_list.add(item1);
        items_list.add(item2);
        adapter = new ArrayAdapter<Item>(this, R.layout.my_items_list_view, items_list);

        // setting up the list view to have an item click listener
        ListView LV = (ListView) findViewById(R.id.myItemsListView);
        LV.setAdapter(adapter);
        LV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewMyItems.this, ViewItem.class);
                Gson gson = new Gson();
                String fuel_purchase_list = gson.toJson(items_list);
                intent.putExtra("list_as_string", fuel_purchase_list);
                String purchase_pos = String.valueOf(position);
                intent.putExtra("position_as_string", purchase_pos);
                // added a mode by asking for the ViewItem class's named integer, so it's easy to understand
                String mode = Integer.toString(ViewItem.MODE_EDIT);
                intent.putExtra("mode", mode);
                startActivity(intent);
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
                startActivity(intent);
            }
        });
    }
}
