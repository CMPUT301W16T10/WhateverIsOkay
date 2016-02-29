package com.example.suhussai.gameshare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by dan on 2016-02-21.
 */
public class ViewItem extends AppCompatActivity{

    private EditText GameName;
    private EditText Players;
    private EditText Age;
    private EditText TimeReq;
    private EditText Platform;

    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        //TODO: set item to be the Item object being displayed when view is loaded (could be done on controller side)

        GameName = (EditText) findViewById(R.id.AddItem_NameEdit);
        Players = (EditText) findViewById(R.id.AddItem_PlayersEdit);
        Age = (EditText) findViewById(R.id.AddItem_AgeEdit);
        TimeReq = (EditText) findViewById(R.id.AddItem_TimeReqEdit);
        Platform = (EditText) findViewById(R.id.AddItem_PlatformEdit);

        Button SaveButton = (Button) findViewById(R.id.AddItem_Save);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try {

                    String name = GameName.getText().toString();
                    String players = Players.getText().toString();
                    String age = Age.getText().toString();
                    String timeReq = TimeReq.getText().toString();
                    String platform = Platform.getText().toString();

                    //TODO modify this so the user is known at this stage. Using a test user in interim.
                    User user = new User("testuser", "testpass");

                    // TODO the controller may need to be involved here.
                    Item item = new Item(name, user, players, age, timeReq, platform);
                    user.addItem(item);

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item has been successfully added", Toast.LENGTH_LONG );
                    toast.show();
                } catch (Exception e) {

                }
            }
        });

        Button CancelButton = (Button) findViewById(R.id.AddItem_Cancel);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                try{
                    // TODO we may wish to instead go back to the user profile here.
                    GameName.setText("");
                    Players.setText("");
                    Age.setText("");
                    TimeReq.setText("");
                    Platform.setText("");

                    // Accessed http://developer.android.com/guide/topics/ui/notifiers/toasts.html on 2016-02-28 for help with pop up messages
                    Toast toast = Toast.makeText(getApplicationContext(), "Item addition has been cancelled", Toast.LENGTH_LONG );
                    toast.show();

                } catch (Exception e) {

                }
            }
        });

        Button DeleteButton = (Button) findViewById(R.id.AddItem_Delete);

        //TODO probably a better way to do this
        //'this' needs to be accessed by AlertDialog.Builder, but it is inside an OnClickListener
        //so the this keyword is overwritten
        final Context holder = this;

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(holder);
                adBuilder.setMessage("Are you sure you want to delete this item?");
                adBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO modify this so the user is known at this stage. Using a test user in interim.
                        User user = new User("testuser", "testpass");
                        user.deleteItem(item);
                    }
                });
                adBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = adBuilder.create();
                ad.show();
            }
        });
    }

}
