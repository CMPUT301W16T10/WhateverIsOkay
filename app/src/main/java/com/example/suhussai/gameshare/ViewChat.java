package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ViewChat extends AppCompatActivity {

    private EditText messageEditText;
    private ListView chatLV;
    private ArrayList<Chat> chatList = new ArrayList<Chat>();
    private User user;
    protected String friend;
    private Date date;
    private Chat chat;
    private String message;
    private ArrayAdapter<Chat> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat);

        chatLV = (ListView) findViewById(R.id.chatListView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        Intent intent = getIntent();
        friend = intent.getStringExtra("friend");

        chatLV.setAdapter(adapter);

        // Grab the user from the controller.
        UserController.GetUser getUser = new UserController.GetUser();
        getUser.execute(UserController.getCurrentUser().getUsername());

        // Fills in the places needed to be filled for the User Profile
        try {
            user = getUser.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
    }

    private void sendMessage(){
        String message = messageEditText.getText().toString();
        chat = new Chat(user, friend, message, date);
        chatList.add(chat);
//        adapter.notifyDataSetChanged();
        messageEditText.setText("");
    }
}
