package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.AboutUsActivity;
import com.microsoft.projectoxford.face.samples.CalendarActivity;
import com.microsoft.projectoxford.face.samples.CourseActivity;
import com.microsoft.projectoxford.face.samples.MainActivity;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.UUID;

//settings activity
public class SettingsActivity extends ActionBarActivity {
    TextView password,name,email,hello;
    Button restore,change_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        //initialize
        name = (TextView)findViewById(R.id.username);
        hello = (TextView)findViewById(R.id.info);
        email = (TextView)findViewById(R.id.email);
        restore = (Button)findViewById(R.id.restore);
        change_password = (Button)findViewById(R.id.change_password);
        hello.setText("hi "+ParseUser.getCurrentUser().getUsername()+",");
        name.setText(ParseUser.getCurrentUser().getUsername());
        email.setText(ParseUser.getCurrentUser().getEmail());

        //restore the password
        restore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ParseUser.requestPasswordResetInBackground(ParseUser.getCurrentUser().getEmail(),
                        new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(SettingsActivity.this, "An email was successfully " +
                                            "sent with reset instructions.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                    // Something went wrong. Look at the ParseException to see what's up.
                                }
                            }
                        });

            }
        });

        //change password
        change_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
                View promptsView = li.inflate(R.layout.password_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        SettingsActivity.this);

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

                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                        currentUser.setPassword(userInput.getText().toString());
                                        currentUser.saveInBackground();
                                        Toast.makeText(SettingsActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();




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

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_signOut) {
            ParseUser.logOutInBackground();
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);

            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(SettingsActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(SettingsActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(SettingsActivity.this,PersonGroupActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(SettingsActivity.this,MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
