//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Project Oxford: http://ProjectOxford.ai
//
// ProjectOxford SDK Github:
// https://github.com/Microsoft/ProjectOxfordSDK-Windows
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity {

        private Button signButton,registerNowButton;
        private TextView userName,password,helloGuest;
        private EditText passwordField,userNameField;
        private ImageView logo;
        private String usernametxt,passwordtxt,emailtxt;
        private boolean run= false;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if(!run) {
                //Parse initialize
                Parse.enableLocalDatastore(this);
                Parse.initialize(this);
                ParseUser.enableAutomaticUser();
                ParseACL defaultACL = new ParseACL();
                defaultACL.setPublicReadAccess(true);
                ParseACL.setDefaultACL(defaultACL, true);

                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
            }
            run = true;
            signButton = (Button)findViewById(R.id.signButton);
            registerNowButton = (Button)findViewById(R.id.registerButton);
            userName = (TextView)findViewById(R.id.userNameLable);
            password = (TextView)findViewById(R.id.passwordLable);
            helloGuest =(TextView)findViewById(R.id.helloGuestLable);
            passwordField=(EditText)findViewById(R.id.passwordFieldText);

            userNameField=(EditText)findViewById(R.id.userNameFieldText);

            logo=(ImageView)findViewById(R.id.logo);


            registerNowButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);

                }
            });
            signButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            // Retrieve the text entered from the EditText
                            usernametxt = userNameField.getText().toString();
                            passwordtxt = passwordField.getText().toString();


                            // Send data to Parse.com for verification
                            ParseUser.logInInBackground(usernametxt, passwordtxt,
                                    new LogInCallback() {
                                        public void done(ParseUser user, ParseException e) {
                                            if (user != null) {
                                                // If user exist and authenticated, send user to Welcome.class
                                                Intent intent = new Intent(
                                                        MainActivity.this,
                                                        IdentificationActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(getApplicationContext(),
                                                        "Successfully Logged in",
                                                        Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "No such user exist, please signup",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });


        }






}
