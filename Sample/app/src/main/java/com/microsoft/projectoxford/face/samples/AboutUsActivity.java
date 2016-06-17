package com.microsoft.projectoxford.face.samples;

import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.parse.ParseUser;

import java.util.UUID;

//about us activity
public class AboutUsActivity extends ActionBarActivity {

    Button contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        contact = (Button)findViewById(R.id.contact_us);
        setTitle("About us");

    //send mail.
        contact.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                String[] TO = {"TamarEliyahou@gmail.com"};
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject" );
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
                sendIntent.setData(Uri.parse("mailto:"));
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, TO);

                startActivity(sendIntent);




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
            Intent intent = new Intent(AboutUsActivity.this,MainActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){

            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(AboutUsActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(AboutUsActivity.this,PersonGroupActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(AboutUsActivity.this,MenuActivity.class);

            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(AboutUsActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
