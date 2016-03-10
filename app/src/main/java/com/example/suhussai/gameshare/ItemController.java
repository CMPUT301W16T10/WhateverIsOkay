package com.example.suhussai.gameshare;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by dan on 2016-02-21.
 */
public class ItemController {
    private static JestDroidClient client;

    private static Item currentItem;

    public static Item getCurrentItem() {
        return currentItem;
    }

    public static void setCurrentItem(Item currentItem) {
        ItemController.currentItem = currentItem;
    }

    // If no client, add a client (from lonelyTwitter)
    public static void verifyConfig(){
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080"); // shove your url  in there
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }


    // Adding Item to cmput301wi16t10/items. (reference: lonelyTwitter)
    public static class AddItem extends AsyncTask<Item, Item, Void>{

        @Override
        // Grabs the user, gameCount, updates gameCount on user, and generates a newId.
        // reference: https://androidresearch.wordpress.com/2012/03/17/understanding-asynctask-once-and-forever/
        protected void onProgressUpdate(Item... values){
            super.onProgressUpdate();

        }

        @Override
        protected Void doInBackground(Item... params){
            verifyConfig();

            for (Item item : params){
                // pass item to onProgressUpdate to get user, update gameCount and get gameCount
                // Apparently implemented in user.addItem() so commented out for now
                //publishProgress(item);

                System.out.println(item.getName() + " " + item.getOwner());

                Index index = new Index.Builder(item).index("cmput301wi16t10").type("items").id(item.getId()).build();

                try {
                    JestResult execute = client.execute(index);
                    // DocumentResult execute = client.execute(index);
                    if (execute.isSucceeded()) {
                    } else {
                        Log.e("TODO", "Our insert of item failed, oh no!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    // obtain a list of items that match the criteria (owned by owner). reference: lonelyTwitter
    // params[0] = mode
    // params[1] = 0. owner, 1. borrower, 2. user, 3. keyword, 4. user, 5. owner (depending on mode)
    public static class GetItems extends AsyncTask<String, Void, ArrayList<Item>> {

        // used to populate the list of user's items
        public static final String MODE_GET_MY_ITEMS = "0";
        // used to populate the list of others' items the user has borrowed
        public static final String MODE_GET_BORROWED_ITEMS = "1";
        // used to populate the search with all items except the user's
        public static final String MODE_POPULATE_SEARCH = "2";
        // used to repopulate the searched list to narrow it down using the partial matching keyword
        public static final String MODE_SEARCH_KEYWORD = "3";
        // used to populate the list of others' items the user has placed bids on
        public static final String MODE_GET_BIDDED_ITEMS = "4";
        // used to populate the list of user's items with bids on them
        public static final String MODE_GET_MY_ITEMS_WITH_BIDS = "5";

        private String search_items;


        @Override
        protected ArrayList<Item> doInBackground(String... params) {
            verifyConfig();

            // NOTE: A HUGE ASSUMPTION IS ABOUT TO BE MADE
            // Assume that only one string is passed in.

            // To hold the items belonging to the user
            ArrayList<Item> item_list = new ArrayList<Item>();

            String mode = params[0];

            if (mode.equals(MODE_GET_MY_ITEMS)) {
                search_items = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"owner\":\"" + params[1] + "\"}}}";
            }

            else if (mode.equals(MODE_GET_BORROWED_ITEMS)) {
                search_items = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"borrower\":\"" + params[1] + "\"}}}";
            }

            else if (mode.equals(MODE_POPULATE_SEARCH) || mode.equals(MODE_GET_BIDDED_ITEMS)) {
                // not owner and borrowed == false
                search_items =  " {\n" +
                                " \"from\": 0,\n" +
                                " \"size\": 10000,\n" +
                                " \"query\":{\n" +
                                "  \t\"match\": {\"borrowed\" : false }\n" +
                                "  }, \n" +
                                "  \"filter\":{ \n" +
                                "  \t\"not\": { \"term\": {\"owner\" :  \""+params[1]+"\"} }\n" +
                                "\t}\n" +
                                "}";
            }

            else if (mode.equals(MODE_SEARCH_KEYWORD)) {
                //TODO: insert search_string that does partial matching to item name here.
                search_items = "";
            }

            else if (mode.equals(MODE_GET_MY_ITEMS_WITH_BIDS)) {
                search_items =  "{\n" +
                                " \"from\": 0,\n" +
                                " \"size\": 10000,\n" +
                                " \"query\":{\n" +
                                "  \t\"match\": {\"bidded\" : true }\n" +
                                "  }, \n" +
                                "  \"filter\":{ \n" +
                                "  \t\"not\": { \"term\": {\"owner\" :  \""+params[1]+"\"} }\n" +
                                "\t}\n" +
                                "}";
            }

            Search search = new Search.Builder(search_items).addIndex("cmput301wi16t10").addType("items").build();

            try {
                SearchResult execute = client.execute(search);
                if (execute.isSucceeded()) {
                    List<Item> foundItems = execute.getSourceAsObjectList(Item.class);
                    item_list.addAll(foundItems);

                    if (mode.equals(MODE_GET_BIDDED_ITEMS)) {
                        //performed populate search, check bids on all items, if bidder == user, add to new list then return new list.
                        // item_list2 = new array to be returned.
                        ArrayList<Item> item_list2 = new ArrayList<Item>();
                        // for each item in item_list
                        for (int i = 0 ; i < item_list.size() ; i++) {
                            Item temp_item = item_list.get(i);
                            ArrayList<Bid> temp_bids_list = temp_item.getBids();
                            // for each bid in bids_list of the current item
                            for (int j = 0 ; j < temp_bids_list.size(); j++) {
                                Bid temp_bid = temp_bids_list.get(j);
                                // if bidder == username, add item to item_list2 and break
                                if (temp_bid.getBidder() != null) {
                                    if (temp_bid.getBidder().equals(params[1])) {
                                        item_list2.add(temp_item);
                                        break;
                                    }
                                }
                            }
                        }
                        // terminates here if we are getting bidded items.
                        return item_list2;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return item_list;
        }
    }

    //TODO: Create an UpdateItem class to use for Editing an Item
    public static class UpdateItem extends AsyncTask<Item, Void, Void> {

        @Override
        protected Void doInBackground(Item... params) {
            verifyConfig();

            for (Item item : params) {
                Index update = new Index.Builder(item).index("cmput301wi16t10").type("items").id(item.getId()).build();

                try {
                    JestResult execute = client.execute(update);

                    if (execute.isSucceeded()) {
                        //yay... we don't need this if statement for now
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
