package com.example.suhussai.gameshare;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Bruce-PC on 3/29/2016.
 */
public class Chat {

    private User user;
    private String friend;
    private String Id = "";
    private String message;
    private String username;
    private Date date;
    private ArrayList<Chat> chats = new ArrayList<>();

    public Chat(User user, String friend, String message, Date date){
        this.user = user;
        this.friend = friend;
        this.message = message;
        this.date = date;
    };

    public Chat(String message, Date date){
        this.message = message;
        this.date = date;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return username = user.getUsername();
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }


    /**
     * Adds a new chat to the user
     * @param chat the new chat
     */
    public void addChat(Chat chat){

        // Refresh the chat list owned by this user in case of any recent changes
        // Grab the chat from the controller.
        ChatController.GetChat getChats = new ChatController.GetChat();
        getChats.execute(getChats.MODE_GET_MY_CHAT, user.getUsername());

        ChatController.setCurrentChat(this);

        try {
            chats = getChats.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // set to chat's ID before sending to controller
        chat.setId(user.getUsername() + '_' + friend);
        chats.add(chat);

        // Post the new item information
        // Set the item via the controller.
        ChatController.AddChat addChat = new ChatController.AddChat();
        addChat.execute(chat);

        // Adds the Chat to the user
        ChatController.UpdateChat updateChat = new ChatController.UpdateChat();
        updateChat.execute(this);
    }

    /**
     * Deletes the specified chat
     * @param chat the chat
     */
    public void deleteItem(Chat chat){
        ChatController.DeleteChat deleteChat = new ChatController.DeleteChat();
        deleteChat.execute(chat);
        chats.remove(chat);
    }



}
