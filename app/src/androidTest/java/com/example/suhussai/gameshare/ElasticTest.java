package com.example.suhussai.gameshare;

import android.test.ActivityInstrumentationTestCase2;

import java.util.concurrent.ExecutionException;

/**
 * Created by bobby on 07/03/16.
 */
public class ElasticTest extends ActivityInstrumentationTestCase2 {

    public ElasticTest() {
        super(ElasticsearchController.class);
    }

    public void testAdd_Get_DeleteItem(){
        User user = new User("owner1", "pass");

        Item item = new Item("Item1", user);
        item.setId("9389");

        Item item2 = new Item("Item132", user);
        item2.setId("903");

        ElasticsearchController.AddItem addItem = new ElasticsearchController.AddItem();
        addItem.execute(item, item2);

        ElasticsearchController.GetItemById getItemById = new ElasticsearchController.GetItemById();
        getItemById.execute("9389");
        Item itemFound = null;
        try {
            itemFound = getItemById.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertTrue(itemFound != null);
        assertEquals(item.toString(), itemFound.toString());


        ElasticsearchController.GetItemById getItemById2 = new ElasticsearchController.GetItemById();
        getItemById2.execute("903");
        itemFound = null;
        try {
            itemFound = getItemById2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertTrue(itemFound != null);
        assertEquals(item2.toString(), itemFound.toString());

        ElasticsearchController.DeleteItemById deleteItemById =
                new ElasticsearchController.DeleteItemById();
        deleteItemById.execute("903");

        ElasticsearchController.GetItemById getItemById3 = new ElasticsearchController.GetItemById();
        getItemById3.execute("903");
        itemFound = null;
        try {
            itemFound = getItemById3.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(null, itemFound);

    }

}
