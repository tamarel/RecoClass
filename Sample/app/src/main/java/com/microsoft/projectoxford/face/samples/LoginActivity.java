package com.microsoft.projectoxford.face.samples;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends Activity {
    // Declare Variables
    ImageButton signup,reset_password;
    String usernametxt;
    String passwordtxt;
    String emailtxt;
    String nametxt;
    EditText password;
    EditText name;
    EditText username;
    EditText email;
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.activity_login);
        // Locate EditTexts in main.xml
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        // Locate Buttons in main.xml
        signup = (ImageButton) findViewById(R.id.signup);

        // Sign up Button Click Listener
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                emailtxt = email.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") || passwordtxt.equals("") || emailtxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
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
                                        "Sign up Error ", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

    }
}
