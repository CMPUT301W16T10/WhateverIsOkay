package com.example.suhussai.gameshare;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;

import static io.searchbox.core.Index.*;

/**
 * Created by sangsoo on 04/03/16.
 */
public class ElasticsearchController {

    private static JestDroidClient client;

    // If no client, add a client (from lonelyTwitter)
    public static void verifyConfig(){
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://gameshare-umtest.rhcloud.com/"); // shove your url  in there
//            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080/"); // shove your url  in there
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    public static class AddItem extends AsyncTask<Item,Void,Void> {

        @Override
        protected Void doInBackground(Item... params) {
            verifyConfig();

            for(Item item : params) {
//                Index index = new Index.Builder(item).index("games").type("game").id(item.getId()).build();
                Index index = new Index.Builder(item).index("games").type("game").build();

                try {
                    DocumentResult execute = client.execute(index);
                    if(execute.isSucceeded()) {
                        // item.setId(execute.getId());
                    } else {
                        Log.e("TODO", "Our insert of tweet failed, oh no!: "+execute.getErrorMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public static class UpdateItem extends AsyncTask<Item,Void,Void> {

        @Override
        protected Void doInBackground(Item... params) {
            verifyConfig();

            for(Item item : params) {
                Update index = new Update.Builder(item).index("games").type("game").id(item.getId()).build();

                try {
                    DocumentResult execute = client.execute(index);
                    if(execute.isSucceeded()) {
                        item.setId(execute.getId());
                    } else {
                        Log.e("TODO", "Our update of tweet failed, oh no!: "+execute.getErrorMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public static class GetItemById extends AsyncTask<String,Void,Item> {

        @Override
        protected Item doInBackground(String... params) {
            verifyConfig();
            Item foundItem = null;
            Get get = new Get.Builder("games",params[0]).type("game").build();

            try {
                DocumentResult execute = client.execute(get);
                if(execute.isSucceeded()) {
                    foundItem = execute.getSourceAsObject(Item.class);
                    // item.setId(execute.getId());
                } else {
                    Log.e("TODO", "Our getting of tweet failed, oh no!: " + execute.getErrorMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return foundItem;
        }
    }



    public static class DeleteItemById extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            verifyConfig();

            for(String item : params) {
                Delete delete = new Delete.Builder(params[0])
                        .index("games")
                        .type("game")
                        .build();

                try {
                    DocumentResult execute = client.execute(delete);
                    if(execute.isSucceeded()) {
                        Log.i("TODO", "Deleted ID:" + params[0]);
                    } else {
                        Log.e("TODO", "Our delete of tweet failed, oh no!: "+execute.getErrorMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }


    public static class SearchForItemsByName extends AsyncTask<String,Void,ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(String... params) {
            verifyConfig();

            // Hold (eventually) the tweets that we get back from Elasticsearch
            ArrayList<Item> items = new ArrayList<Item>();

            // NOTE: A HUGE ASSUMPTION IS ABOUT TO BE MADE!
            // Assume that only one string is passed in.

            // The following gets the top "10000" tweets
            //String search_string = "{\"from\":0,\"size\":10000}";

            // The following searches for the top 10 tweets matching the string passed in (NOTE: HUGE ASSUMPTION PREVIOUSLY)
            //String search_string = "{\"query\":{\"match\":{\"message\":\"" + params[0] + "\"}}}";

            // The following gets the top 10000 tweets matching the string passed in
            String search_string = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"name\":\"" + params[0] + "\"}}}";

            Search search = new Search.Builder(search_string).addIndex("games").addType("game").build();
            try {
                SearchResult execute = client.execute(search);
                if(execute.isSucceeded()) {
                    List<Item> foundTweets = execute.getSourceAsObjectList(Item.class);
                    items.addAll(foundTweets);
                } else {
                    Log.i("TODO", "Search was unsuccessful, do something!: " + execute.getErrorMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public static class SearchForItemsByUser extends AsyncTask<String,Void,ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(String... params) {
            verifyConfig();

            // Hold (eventually) the tweets that we get back from Elasticsearch
            ArrayList<Item> items = new ArrayList<Item>();

            // NOTE: A HUGE ASSUMPTION IS ABOUT TO BE MADE!
            // Assume that only one string is passed in.

            // The following gets the top "10000" tweets
            //String search_string = "{\"from\":0,\"size\":10000}";

            // The following searches for the top 10 tweets matching the string passed in (NOTE: HUGE ASSUMPTION PREVIOUSLY)
            //String search_string = "{\"query\":{\"match\":{\"message\":\"" + params[0] + "\"}}}";

            // The following gets the top 10000 tweets matching the string passed in
            String search_string = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"user\":\"" + params[0] + "\"}}}";

            Search search = new Search.Builder(search_string).addIndex("games").addType("game").build();
            try {
                SearchResult execute = client.execute(search);
                if(execute.isSucceeded()) {
                    List<Item> foundTweets = execute.getSourceAsObjectList(Item.class);
                    items.addAll(foundTweets);
                } else {
                    Log.i("TODO", "Search was unsuccessful, do something!: " + execute.getErrorMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return items;
        }
    }


}
