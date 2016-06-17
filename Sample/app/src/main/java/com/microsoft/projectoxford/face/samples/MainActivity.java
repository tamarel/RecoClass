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

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.net.PasswordAuthentication;
import java.security.KeyStore;

public class MainActivity extends ActionBarActivity {

    public boolean isSmsPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }

    }

        public static final String PREFS_NAME = "MyPrefsFile";
        private static  String TYPE = "NONE";
        private ImageButton signButton,registerNowButton;
        private TextView forgot_password,password,helloGuest;
        private EditText passwordField,userNameField;
        private ImageView logo;
        private String usernametxt,passwordtxt,emailtxt;
        private boolean run= true;
        ProgressDialog progressDialog;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            isSmsPermissionGranted();
            final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String userName = settings.getString("userName", "");
            String password = settings.getString("password", "");
            passwordField=(EditText)findViewById(R.id.passwordFieldText);
            userNameField=(EditText)findViewById(R.id.userNameFieldText);
            forgot_password=(TextView)findViewById(R.id.forgot_password);
            passwordField.setText(password);
            userNameField.setText(userName);

            signButton = (ImageButton)findViewById(R.id.signButton);
            registerNowButton = (ImageButton)findViewById(R.id.registerButton);

            helloGuest =(TextView)findViewById(R.id.helloGuestLable);


            logo=(ImageView)findViewById(R.id.logo);
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.progress_dialog_title));
            userNameField.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);


        userNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (userNameField.getText().toString().trim().equalsIgnoreCase("")) {
                        userNameField.setError("Oops! you need to fill this field");


                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                    return false;
                }

                return false;
            }

        });

            //forgot password action
            forgot_password.setOnClickListener(new View.OnClickListener()

           {
               public void onClick(View v) {


                   LayoutInflater li = LayoutInflater.from(MainActivity.this);
                   View promptsView = li.inflate(R.layout.email_prompt, null);

                   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                           MainActivity.this);

                   // set prompts.xml to alertdialog builder
                   alertDialogBuilder.setView(promptsView);

                   final EditText userInput = (EditText) promptsView
                           .findViewById(R.id.editTextDialogUserInput);

                   // set dialog message
                   alertDialogBuilder
                           .setCancelable(false)
                           .setPositiveButton("OK",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           // get user input and set it to result
                                           // edit text
                                           //result.setText(userInput.getText());
                                           if (!userInput.getText().toString().equals("")) {
                                               ParseUser.requestPasswordResetInBackground(userInput.getText().toString(),
                                                       new RequestPasswordResetCallback() {
                                                           public void done(ParseException e) {
                                                               if (e == null) {
                                                                   Toast.makeText(MainActivity.this, "An email was successfully " +
                                                                           "sent with reset instructions.", Toast.LENGTH_LONG).show();
                                                               } else {
                                                                   Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                                                   // Something went wrong. Look at the ParseException to see what's up.
                                                               }
                                                           }
                                                       });
                                           } else
                                               Toast.makeText(getApplicationContext(), "sorry you must enter your mail", Toast.LENGTH_SHORT).show();

                                       }
                                   })
                           .setNegativeButton("Cancel",
                                   new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();
                                       }
                                   });

                   // create alert dialog
                   AlertDialog alertDialog = alertDialogBuilder.create();

                   // show it
                   alertDialog.show();


               }
           }

            );

            registerNowButton.setOnClickListener(new View.OnClickListener()

                                                 {
                                                     public void onClick(View v) {

                 Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                 startActivity(intent);

             }
         }

            );


            signButton.setOnClickListener(new View.OnClickListener()

                                          {

                                              public void onClick(View arg0) {
                  usernametxt = userNameField.getText().toString();
                  passwordtxt = passwordField.getText().toString();
                  if (!usernametxt.equals(settings.getString("userName","")) ||
                          !passwordtxt.equals(settings.getString("password",""))) {
                      open(getCurrentFocus());
                  }
                  else {
                      // Retrieve the text entered from the EditText


                      progressDialog.setMessage("please wait...");
                      progressDialog.show();
                      connect();
                  }




                  }

          }

            );


        }

    public void open(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to save your password?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userName", userNameField.getText().toString());
                editor.putString("password", passwordField.getText().toString());


                // Commit the edits!
                editor.commit();
                connect();
                }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connect();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void connect(){
        ParseUser.logInInBackground(usernametxt, passwordtxt,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // If user exist and authenticated, send user to Welcome.class
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    MenuActivity.class);

                            intent.putExtra("userName", user.getUsername());
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),
                                    "Successfully Logged in",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            finish();
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(
                                    getApplicationContext(),
                                    "No such user exist, please signup",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
