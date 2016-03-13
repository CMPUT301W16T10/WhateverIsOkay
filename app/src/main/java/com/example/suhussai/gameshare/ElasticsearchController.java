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
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import static io.searchbox.core.Index.*;

/**
 * Controller for doing elastic search
 * @deprecated
 */
public class ElasticsearchController {

    /**
     * The client
     * @deprecated
     */
    private static JestDroidClient client;

    /**
     * adds the client if there isn't one already (from lonelyTwitter)
     * @deprecated
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

}
