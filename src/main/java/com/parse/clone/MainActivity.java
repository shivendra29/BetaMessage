/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.clone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity{

    Boolean login_mode = false;



    public void redirectlogin() {

        if(ParseUser.getCurrentUser() != null )
        {

            Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
            startActivity(intent);
            finish();

        }
    }

    public void togglelogin(View view) {

        Button button = (Button) findViewById(R.id.signup_button);
        TextView textView = (TextView) findViewById(R.id.login_text);

        if (login_mode) {
            login_mode = false;
            button.setText("Sign Up");
            textView.setText("or, Login");

        } else {

            login_mode = true;
            button.setText("Login");
            textView.setText("or, Sign Up!");


        }
    }


    public void signupLogin(View view) {

        EditText username = (EditText) findViewById(R.id.user_name_edit);
        EditText password = (EditText) findViewById(R.id.password_edit);

        if (login_mode) {

            ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.d("Info", "Logged In");
                        redirectlogin();
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {


            ParseUser user = new ParseUser();
            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Info", "Signed Up!");
                        redirectlogin();
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    }
                }

            });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();

        setContentView(R.layout.activity_main);


//        RelativeLayout backgroud_relative = (RelativeLayout) findViewById(R.id.relativeLayout);
//        backgroud_relative.setOnClickListener(this);
//
//        EditText password = (EditText) findViewById(R.id.password_edit);
//        password.setOnKeyListener(this);

        //redirectlogin();


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}