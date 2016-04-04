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
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Controller for actions involving items. Used to search for items, add new items, delete items,
 * and update existing items. Uses elastic search.
 * @see Item
 */
public class ItemController {
    /**
     * The client
     */
    private static JestDroidClient client;

    /**
     * The item currently being operated on
     */
    private static Item currentItem;

    /**
     * Gets the current item
     * @return the current item
     */
    public static Item getCurrentItem() {
        return currentItem;
    }

    /**
     * Sets the current item
     * @param currentItem the current item
     */
    public static void setCurrentItem(Item currentItem) {
        ItemController.currentItem = currentItem;
    }

    /**
     * Adds the client if there isn't one already (from lonelyTwitter)
     */
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
    /**
     * Adds an item to the online database (cmput301wi16t10/items)
     */
    public static class AddItem extends AsyncTask<Item, Item, Void>{

        /** Grabs the user, gameCount, updates gameCount on user, and generates a newId.
         * reference: https://androidresearch.wordpress.com/2012/03/17/understanding-asynctask-once-and-forever/
         */
        @Override
         protected void onProgressUpdate(Item... values){
            super.onProgressUpdate();

        }

        /**
         * Executes the elastic search
         * @param params search parameters
         */
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

    /**
     * obtain a list of items that match the criteria (owned by owner). reference: lonelyTwitter
     * params[0] = mode
     * params[1] = 0. owner, 1. borrower, 2. user, 3. keyword, 4. user, 5. owner (depending on mode)
     */
    public static class GetItems extends AsyncTask<String, Void, ArrayList<Item>> {

        /**
         * used to populate the list of user's items
         */
        public static final String MODE_GET_MY_ITEMS = "0";
        /**
         * used to populate the list of others' items the user has borrowed
         */
        public static final String MODE_GET_BORROWED_ITEMS = "1";
        /**
         * used to populate the search with all items except the user's
         */
        public static final String MODE_POPULATE_SEARCH = "2";
        /**
         * used to repopulate the searched list to narrow it down using the partial matching keyword
         */
        public static final String MODE_SEARCH_KEYWORD = "3";
        /**
         * used to populate the list of others' items the user has placed bids on
         */
        public static final String MODE_GET_BIDDED_ITEMS = "4";
        /**
         *  used to populate the list of user's items with bids on them
         */
        public static final String MODE_GET_MY_ITEMS_WITH_BIDS = "5";
        /**
         *  used to populate the list of user's items with bids on them
         */
        public static final String MODE_GET_MY_LENT_ITEMS = "6";

        /**
         * the search string
         */
        private String search_items;

        /**
         * Ececutes the search
         * @param params search parameters
         * @return list of items
         */
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
                // http://stackoverflow.com/questions/23681478/multiple-wildcards-in-one-query-in-elasticsearch
                // 9 March 2016, user Alcanzar
                // http://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener.html#onQueryTextChange%28java.lang.String%29
                // http://www.codota.com/android/scenarios/52fcbd6dda0a535f4ee25691/android.widget.SearchView?tag=dragonfly
                // http://joelabrahamsson.com/elasticsearch-101/
                // https://www.elastic.co/guide/en/elasticsearch/guide/1.x/_wildcard_and_regexp_queries.html
                //TODO: insert search_string that does partial matching to item name here.
                search_items = "{\n" +
                        "  \"from\": 0,\n" +
                        "  \"size\": 10000,\n" +
                        "  \"query\": {\n" +
                        "    \"bool\": {\n" +
                        "      \"must\": [\n" +
                        "       {\"match\": {\"borrowed\" : false }},\n";

                String owner = UserController.getCurrentUser().getUsername();

                String[] keywords = params[1].toLowerCase().split(" ");
                for (String keyword : keywords) {
                    if (keyword.equals(keywords[keywords.length - 1])) {
                        // last term to add
                        // does not require comma
                        search_items = search_items +
                                "        {\n" +
                                "          \"wildcard\": {\n" +
                                "            \"name\": \"*" + keyword + "*\"\n" +
                                "          }\n" +
                                "        }\n";
                    } else {
                        // not the last term to add
                        // requires comma
                        search_items = search_items +
                                "        {\n" +
                                "          \"wildcard\": {\n" +
                                "            \"name\": \"*" + keyword + "*\"\n" +
                                "          }\n" +
                                "        },\n";
                    }

                }
                search_items = search_items +
                        "      ]\n" +   // must end
                        "    }\n" + // bool end
                        "  },\n" +   // query end
                        "  \"filter\":{ \n" +
                        "  \t\"not\": { \"term\": {\"owner\" :  \""+owner+"\"} }\n" +
                        "\t}\n" +
                        "}"; // end string
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
            else if (mode.equals(MODE_GET_MY_LENT_ITEMS)) {
                search_items =  "{\n" +
                        "    \"from\":0,\n" +
                        "    \"size\":10000,\n" +
                        "    \"query\": {\n" +
                        "                   \"bool\" : {\n" +
                        "                                  \"must\": [\n" +
                        "                                                {\"match\" : {\n" +
                        "                                                               \"owner\" :  \""+params[1]+"\"\n" +
                        "                                                             }\n" +
                        "                                                },\n" +
                        "                                                {\"match\" : {\n" +
                        "                                                                \"borrowed\":true\n" +
                        "                                                             }\n" +
                        "                                                }\n" +
                        "                                            ]\n" +
                        "                              }\n" +
                        "               }\n" +
                        "}\n";
            }

            Search search = new Search.Builder(search_items).addIndex("cmput301wi16t10").addType("items").build();

            try {
                SearchResult execute = client.execute(search);
                if (execute.isSucceeded()) {
                    List<Item> foundItems = execute.getSourceAsObjectList(Item.class);
                    Log.e("IMP_INFO", "Found "+foundItems.size() + " new items.");
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
                }else {
                    Log.e("IMP_INFO", "Search was not successful.");
                    Log.e("IMP_INFO", execute.getErrorMessage());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return item_list;
        }
    }

    //TODO: Create an UpdateItem class to use for Editing an Item

    /**
     * Updates the given item with new information
     */
    public static class UpdateItem extends AsyncTask<Item, Void, Void> {

        /**
         * Executes the search
         * @param params search parameters
         */
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

    /**
     * Deletes a given item from the database
     */
    public static class DeleteItem extends AsyncTask<Item, Void, Void> {

        /**
         * Executes the elastic search
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(Item... params) {
            verifyConfig();

            for (Item item : params) {
                Delete delete = new Delete.Builder("").index("cmput301wi16t10").type("items").id(item.getId()).build();

                try {
                    JestResult execute = client.execute(delete);

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

    private String applyFilter(String current) {
        //TODO must figure out how to tell what value each filter is set to!
        // platform
        current += "{match";//...
        // min players
        current += "{match";//...

        return current;
    }
}
