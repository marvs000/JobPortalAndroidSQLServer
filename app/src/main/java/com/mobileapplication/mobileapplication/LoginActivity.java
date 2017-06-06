package com.mobileapplication.mobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobileapplication.mobileapplication.util.ToastUtil;

@Deprecated
public class LoginActivity extends AppCompatActivity {



    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /* setup click listener for the login button */
        //setupListeners();
        validateCredentials();
    }

    private void setupListeners(){
        loginButton = (Button) findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCredentials();
            }
        });

    }


    public void validateCredentials(){

        //String password = String.valueOf(((TextView) findViewById(R.id.password)).getText());
        //String email = String.valueOf(((TextView) findViewById(R.id.email)).getText());

        String password = "password";
        String email = "email";

        /* validate credentials here */
        if(email.equals("email") && password.equals("password")){
            /* Toast and switch to activity */
            //ToastUtil.createToast("Login Successful!", this);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("password", password);
            intent.putExtra("email", email);
            startActivity(intent);

        }else{
            ToastUtil.createToast("Incorrect credentials. Please try again.", this);
        }

    }


}
