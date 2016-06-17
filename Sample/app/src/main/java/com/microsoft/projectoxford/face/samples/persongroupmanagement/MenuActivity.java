package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.microsoft.projectoxford.face.samples.AboutUsActivity;
import com.microsoft.projectoxford.face.samples.CalendarActivity;
import com.microsoft.projectoxford.face.samples.MainActivity;
import com.microsoft.projectoxford.face.samples.R;
import com.parse.ParseUser;

import java.util.UUID;


//this class contain buttons .
public class MenuActivity extends ActionBarActivity {
    public GridView grid;
    String userName1;
    ProgressDialog progressDialog;
    public int[] mThumbIds = {
            R.drawable.add_list_button, R.drawable.add_course_button,
            R.drawable.settings_icon, R.drawable.calendar_button,
            R.drawable.about_us_button
    };

    public String[] names = {
            "Courses", "Add course",
            "Settings", "Calendar",
            "About us" };


    //on create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        CustomGrid adapter = new CustomGrid(MenuActivity.this, names, mThumbIds);
        setTitle("Menu");
        Bundle bundle = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_title));
        if (bundle != null) {
            userName1 = bundle.getString("userName");
        }
        grid=(GridView)findViewById(R.id.gridview);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //go to activity
                switch(position){
                    case 0:
                        Intent intent= new Intent(MenuActivity.this,CoursesActivity.class);

                        progressDialog.setMessage("please wait...");
                        progressDialog.show();
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        progressDialog.dismiss();
                        break;
                    case 1:
                        intent= new Intent(MenuActivity.this,PersonGroupActivity.class);
                        intent.putExtra("userName", userName1);
                        intent.putExtra("AddNewPersonGroup",true);
                        String personGroupId = UUID.randomUUID().toString();
                        intent.putExtra("PersonGroupName", "");
                        intent.putExtra("PersonGroupId", personGroupId);
                        startActivity(intent);
                        break;
                    case 2:
                        intent= new Intent(MenuActivity.this,SettingsActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 3:
                        intent= new Intent(MenuActivity.this,CalendarActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 4:

                        intent= new Intent(MenuActivity.this,AboutUsActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent= new Intent(MenuActivity.this,CalendarActivity.class);
                        startActivity(intent);
                        break;



                }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_signOut) {
            ParseUser.logOutInBackground();
            Intent intent = new Intent(MenuActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(MenuActivity.this,AboutUsActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(MenuActivity.this,CalendarActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(MenuActivity.this,PersonGroupActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(MenuActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
