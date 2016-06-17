package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.samples.AboutUsActivity;
import com.microsoft.projectoxford.face.samples.CalendarActivity;
import com.microsoft.projectoxford.face.samples.CourseActivity;
import com.microsoft.projectoxford.face.samples.IdentificationActivity;
import com.microsoft.projectoxford.face.samples.LoginActivity;
import com.microsoft.projectoxford.face.samples.MainActivity;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//this class show the list of courses
public class CoursesActivity extends ActionBarActivity {


    //delete course task
    class DeletePersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Deleting selected person groups...");

                faceServiceClient.deletePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }


    }
    SharedPreferences sharedpreferences;

    private List<CourseProperties> courseList;
    private CustomListAdapter listAdapter;
    private ListView list;
    String userName = "";
    private TextView lectureName ;
    private TextView explain ;
    private ImageButton addCourse;
    private CourseProperties course ;
    public int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // save previous list
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet("key", null);
        editor.commit();// commit is important here.

        addCourse = (ImageButton) findViewById(R.id.addButton);
        explain = (TextView) findViewById(R.id.explain);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName = bundle.getString("userName");
        }
        setTitle("Hello " + userName);

        explain.setText("choose a course please\n");
        list = (ListView) findViewById(R.id.list);

        courseList = new ArrayList<>();
        courseList = StorageHelper.getAllCourseNameByUserName(CoursesActivity.this, userName);

        listAdapter = new CustomListAdapter(this,
                R.layout.list_row, courseList);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list.setAdapter(listAdapter);
        addCourse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String personGroupId = UUID.randomUUID().toString();
                Intent intent = new Intent(getBaseContext(), PersonGroupActivity.class);
                intent.putExtra("AddNewPersonGroup", true);
                intent.putExtra("PersonGroupName", "");

                intent.putExtra("PersonGroupId", personGroupId);
                intent.putExtra("userName", userName);

                startActivity(intent);

            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int index, long arg3) {
                // TODO Auto-generated method stub

                String str = parent.getItemAtPosition(index).toString();
                CourseProperties c = (CourseProperties) parent.getItemAtPosition(index);
                Intent intent = new Intent(CoursesActivity.this, CourseActivity.class);
                intent.putExtra("courseName", c.getCourseName());
                intent.putExtra("courseCode", c.getCourseId());
                intent.putExtra("courseId", StorageHelper.getCourseId(c.getCourseName(),userName,c.getCourseId()));

                intent.putExtra("userName", userName);

                startActivity(intent);
                finish();

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index1, long arg3) {
                index = index1;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoursesActivity.this);
                alertDialogBuilder.setMessage("Do you want to remove this course?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new DeletePersonGroupTask().execute(StorageHelper.getCourseId(courseList.get(index).getCourseName(), userName, courseList.get(index).getCourseId()));
                        StorageHelper.deletePersonGroups(StorageHelper.getCourseId(courseList.get(index).getCourseName(), userName, courseList.get(index).getCourseId()), CoursesActivity.this);
                        Toast.makeText(CoursesActivity.this, "The course removed successfully", Toast.LENGTH_LONG).show();
                        courseList = StorageHelper.getAllCourseNameByUserName(CoursesActivity.this, userName);

                        listAdapter = new CustomListAdapter(CoursesActivity.this,
                                R.layout.list_row, courseList);
                        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        listAdapter.notifyDataSetChanged();
                        list.setAdapter(listAdapter);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



                return true;
            }

        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        list = (ListView)findViewById(R.id.list);

        courseList = new ArrayList<>();
        courseList =  StorageHelper.getAllCourseNameByUserName(CoursesActivity.this, userName);

        listAdapter = new CustomListAdapter(this,
                R.layout.list_row, courseList);
        listAdapter.notifyDataSetChanged();
        list.setAdapter(listAdapter);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
            Intent intent = new Intent(CoursesActivity.this,MainActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(CoursesActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(CoursesActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(CoursesActivity.this,PersonGroupActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(CoursesActivity.this,MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(CoursesActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
