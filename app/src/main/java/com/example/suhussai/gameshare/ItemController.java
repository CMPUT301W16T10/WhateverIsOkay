package com.example.suhussai.gameshare;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import static io.searchbox.core.Index.Builder;

/**
 * Created by dan on 2016-02-21.
 */
public class ItemController {
    private static JestDroidClient client;


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
    public static class AddItem extends AsyncTask<Item, Void, Void>{

        @Override
        protected Void doInBackground(Item... params){
            verifyConfig();

            for (Item item : params){

                Index index = new Builder(item).index("cmput301wi16t10").type("items").build();

                try {
                    DocumentResult execute = client.execute(index);
                    if (execute.isSucceeded()) {
                        item.setId(index.getId());
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
    public static class GetItems extends AsyncTask<String, Void, ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(String... params) {
            verifyConfig();

            // NOTE: A HUGE ASSUMPTION IS ABOUT TO BE MADE
            // Assume that only one string is passed in.

            // To hold the items belonging to the user
            ArrayList<Item> item_list = new ArrayList<Item>();

            String search_items = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"owner\":\"" + params[0] + "\"}}}";

            Search search = new Search.Builder(search_items).addIndex("cmput301wi16t10").addType("items").build();

            try {
                SearchResult execute = client.execute(search);
                if (execute.isSucceeded()) {
                    List<Item> foundTweets = execute.getSourceAsObjectList(Item.class);
                    item_list.addAll(foundTweets);

                    // Add id to item id
                    // reference: http://stackoverflow.com/questions/33352798/elasticsearch-jest-client-how-to-return-document-id-from-hit
                    List<SearchResult.Hit<Map, Void>> hits = client.execute(search).getHits(Map.class);
                    if (hits.size() > 0 && item_list != null) {
                        for (int i = 0; i < hits.size(); i++) {
                            SearchResult.Hit hit = hits.get(i);
                            Map source = (Map) hit.source;
                            item_list.get(i).setId((String) source.get(JestResult.ES_METADATA_ID));
                        }
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