package com.microsoft.projectoxford.face.samples;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

import java.util.Random;

public class LoginActivity extends Activity {
    // Declare Variables
    ImageButton signup,reset_password;
    String usernametxt;
    String passwordtxt;
    String emailtxt,phonetxt;
    String nametxt;
    EditText password,phone;
    EditText name;
    EditText username;
    EditText email;
    int otp;
    boolean firstTime = true;
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.activity_login);
        // Locate EditTexts in main.xml
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);

        // Locate Buttons in main.xml
        signup = (ImageButton) findViewById(R.id.signup);

        // Sign up Button Click Listener
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                emailtxt = email.getText().toString();
                phonetxt = phone.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") || passwordtxt.equals("") || emailtxt.equals("") || phonetxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage

                    if ( firstTime == true) {
                        firstTime = false;
                        Random r = new Random();
                        int number = r.nextInt(9999);
                        otp = number;
                        String messageText = "The code is:" + number;
                        SmsManager smsManager = SmsManager.getDefault();
                      try{
                          smsManager.sendTextMessage(phonetxt, null, messageText, null, null);
                      }
                      catch (SecurityException e){
                          Toast.makeText(getApplicationContext(),
                                  "You must accept the permission", Toast.LENGTH_LONG)
                                  .show();
                          Intent intent = new Intent(
                                  LoginActivity.this,
                                  MainActivity.class);
                          startActivity(intent);
                      }
                    }
                    open();

                }

            }
        });

    }

    public void open(){
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.phone_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                LoginActivity.this);

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
                                String code = userInput.getText().toString();
                                try {
                                    if (code.equals(otp + "")) {

                                        ParseUser user = new ParseUser();
                                        user.setUsername(usernametxt);
                                        user.setPassword(passwordtxt);
                                        user.setEmail(emailtxt);

                                        user.signUpInBackground(new SignUpCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {


                                                    Intent intent = new Intent(
                                                            LoginActivity.this,
                                                            MenuActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(getApplicationContext(),
                                                            "Successfully create user",
                                                            Toast.LENGTH_LONG).show();
                                                    finish();

                                                } else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "error in create user", Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }
                                        });
                                    }else
                                    Toast.makeText(getApplicationContext(), "The code isn't match ", Toast.LENGTH_SHORT).show();

                                }
                                catch (SecurityException e){
                                    Toast.makeText(getApplicationContext(),
                                            "You must accept the permission", Toast.LENGTH_LONG)
                                            .show();
                                    Intent intent = new Intent(
                                            LoginActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);

                                }
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

