package com.example.suhussai.gameshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by sangsoo on 04/03/16.
 */
public class ViewLogIn extends AppCompatActivity {

    private User user;
    private EditText userid;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userid = (EditText) findViewById(R.id.UsernameText);
        pass = (EditText) findViewById(R.id.PasswordText);

        // Login button logs into the App if the username exists and the password matches OR
        // if the username does not exist.
        Button loginButton = (Button) findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                String username = userid.getText().toString();
                String password = pass.getText().toString();

                UserController.GetUser getUser = new UserController.GetUser();
                getUser.execute(username);

                //TODO: Need to check for empty passwords and usernames
                //TODO: Need to not allow duplicate username items to exist in server (make user name unique)

                try {
                    user = getUser.get();

                    // user does not exist, create new user.
                    if (user == null){
                        user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        UserController.AddUser addUser = new UserController.AddUser();
                        addUser.execute(user);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                // new user OR user and the password matches -> login.
                if (user.getUsername().equals(username) &&
                        user.getPassword().equals(password) ){

                    setResult(RESULT_OK);
                    Intent intent = new Intent(ViewLogIn.this, ViewUserProfile.class);
                    intent.putExtra("username", String.valueOf(username));
                    intent.putExtra("mode",ViewUserProfile.MODE_EDIT);
                    startActivity(intent);
                    finish();
                    Toast.makeText(ViewLogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }

                // reject login.
                else {
                    Toast.makeText(ViewLogIn.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}