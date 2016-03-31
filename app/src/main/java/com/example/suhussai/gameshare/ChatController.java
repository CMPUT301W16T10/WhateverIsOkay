package com.example.suhussai.gameshare;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ChatController {
    /**
     * The client
     */
    private static JestDroidClient client;

    /**
     * The chat currently being operated on
     */
    private static Chat currentChat;

    /**
     * Gets the current chat
     *
     * @return the current chat
     */
    public static Chat getCurrentChat() {
        return currentChat;
    }

    /**
     * Sets the current chat
     *
     * @param currentChat the current chat
     */
    public static void setCurrentChat(Chat currentChat) {
        ChatController.currentChat = currentChat;
    }

    /**
     * Adds the client if there isn't one already (from lonelyTwitter)
     */
    public static void verifyConfig() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080"); // shove your url  in there
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    /**
     * Adds an message to the online database (cmput301wi16t10/chats)
     */
    public static class AddChat extends AsyncTask<Chat, Chat, Void> {

        /**
         * Grabs the user, gameCount, updates gameCount on user, and generates a newId.
         * reference: https://androidresearch.wordpress.com/2012/03/17/understanding-asynctask-once-and-forever/
         */
        @Override
        protected void onProgressUpdate(Chat... values) {
            super.onProgressUpdate();

        }

        /**
         * Executes the elastic search
         *
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(Chat... params) {
            verifyConfig();

            for (Chat chat : params) {
                // pass item to onProgressUpdate to get user, update gameCount and get gameCount
                // Apparently implemented in user.addItem() so commented out for now
                //publishProgress(item);

                System.out.println(chat.getId() + " " + chat.getUser());

                Index index = new Index.Builder(chat).index("cmput301wi16t10").type("chats").id(chat.getId()).build();

                try {
                    JestResult execute = client.execute(index);
                    // DocumentResult execute = client.execute(index);
                    if (execute.isSucceeded()) {
                    } else {
                        Log.e("TODO", "Our insert of the chat failed, oh no!");
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
    public static class GetChat extends AsyncTask<String, Void, ArrayList<Chat>> {

        /**
         * used to populate the list of user's chat
         */
        public static final String MODE_GET_MY_CHAT = "0";

        /**
         * the search string
         */
        private String search_chats;

        /**
         * Ececutes the search
         *
         * @param params search parameters
         * @return list of items
         */
        @Override
        protected ArrayList<Chat> doInBackground(String... params) {
            verifyConfig();

            // NOTE: A HUGE ASSUMPTION IS ABOUT TO BE MADE
            // Assume that only one string is passed in.

            // To hold the items belonging to the user
            ArrayList<Chat> chat_list = new ArrayList<Chat>();

            String mode = params[0];

            if (mode.equals(MODE_GET_MY_CHAT)) {
                search_chats = "{\"from\":0,\"size\":10000,\"query\":{\"match\":{\"owner\":\"" + params[1] + "\"}}}";
            }
            Search search = new Search.Builder(search_chats).addIndex("cmput301wi16t10").addType("chats").build();

            try {
                SearchResult execute = client.execute(search);
                if (execute.isSucceeded()) {
                    List<Chat> foundChat = execute.getSourceAsObjectList(Chat.class);
                    Log.e("IMP_INFO", "Found " + foundChat.size() + " new chats.");
                    chat_list.addAll(foundChat);

                } else {
                    Log.e("IMP_INFO", "Search was not successful.");
                    Log.e("IMP_INFO", execute.getErrorMessage());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return chat_list;
        }
    }


    /**
     * Updates the given item with new information
     */
    public static class UpdateChat extends AsyncTask<Chat, Void, Void> {

        /**
         * Executes the search
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(Chat... params) {
            verifyConfig();

            for (Chat chat : params) {
                Index update = new Index.Builder(chat).index("cmput301wi16t10").type("chats").id(chat.getId()).build();

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
    public static class DeleteChat extends AsyncTask<Chat, Void, Void> {

        /**
         * Executes the elastic search
         * @param params search parameters
         */
        @Override
        protected Void doInBackground(Chat... params) {
            verifyConfig();

            for (Chat chat : params) {
                Delete delete = new Delete.Builder("").index("cmput301wi16t10").type("chats").id(chat.getId()).build();

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
}

