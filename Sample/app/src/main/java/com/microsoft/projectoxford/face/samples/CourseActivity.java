package com.microsoft.projectoxford.face.samples;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CourseProperties;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CoursesActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListAdapter;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.DataList;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

// this class show the saved list of attendance.
public class CourseActivity extends ActionBarActivity {

    private ArrayList<String> lists;
    private ArrayAdapter listAdapter;
    private ListView list;
    private DataList data;
    private ImageButton addList;
    private TextView explain ;
    private String courseCode;
    private String courseId;
    private String courseName,userName="";
    private Button manageCourse,queryButton;
    public int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseName = bundle.getString("courseName");
            courseId =bundle.getString("courseId");
            userName =bundle.getString("userName");
            courseCode =bundle.getString("courseCode");
            if (courseName!=null )
                setTitle(courseName);
        }
        list = (ListView)findViewById(R.id.list);
        addList =(ImageButton)findViewById(R.id.addButton);

        lists = StorageHelper.getAllListAttendance(userName,courseName,
                courseId);

            listAdapter = new ArrayAdapter(this, R.layout.date_row, R.id.date,lists);
            listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            list.setAdapter(listAdapter);

        explain = (TextView)findViewById(R.id.explain);
        manageCourse = (Button)findViewById(R.id.manageCourse);
        queryButton = (Button)findViewById(R.id.queryButton);
        explain.setText("   choose a list \nor add a new list");
        explain.setGravity(View.TEXT_ALIGNMENT_CENTER);

        //add list of attendance
        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), IdentificationActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("codeCourse", courseCode);
                intent.putExtra("PersonGroupId",courseId);
                startActivity(intent);

            }
        });

        //manage course
        manageCourse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PersonGroupActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("PersonGroupCode", courseCode);
                intent.putExtra("PersonGroupId", courseId);
                startActivity(intent);

            }
        });

        //go to query activity
        queryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), QueriesActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("PersonGroupCode", courseCode);
                intent.putExtra("PersonGroupId", courseId);
                startActivity(intent);

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index1, long arg3) {
                index = index1;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseActivity.this);
                alertDialogBuilder.setMessage("Do you want to remove this list?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (StorageHelper.deleteList(courseId,lists.get(index).toString() ,CourseActivity.this))
                            Toast.makeText(CourseActivity.this, "The list removed successfully", Toast.LENGTH_LONG).show();
                        else Toast.makeText(CourseActivity.this, "something is wrong", Toast.LENGTH_LONG).show();

                        lists = StorageHelper.getAllListAttendance(userName,courseName,courseId);
                        listAdapter = new ArrayAdapter(CourseActivity.this, R.layout.date_row, R.id.date,lists);
                        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        list.setAdapter(listAdapter);
                        listAdapter.notifyDataSetChanged();


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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int index, long arg3) {
                // TODO Auto-generated method stub

                String str = parent.getItemAtPosition(index).toString();
                Intent intent = new Intent(CourseActivity.this, StudentListActivity.class);
                
                intent.putExtra("courseName", courseName);
                intent.putExtra("courseId",courseId);
                intent.putExtra("userName", userName);
                intent.putExtra("codeCourse", courseCode);
                intent.putExtra("Date", lists.get(index).toString());
         /*       CalendarActivity calendar = CalendarActivity.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(calendar.getTime());*/
                ArrayList<StudentProperties> list = StorageHelper.getAllListAttendanceByid(userName,courseName,
                        courseId,str);

                intent.putExtra("studentList",list);
                startActivity(intent);
                finish();

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
        lists = StorageHelper.getAllListAttendance(userName,courseName,courseId);
        listAdapter = new ArrayAdapter(this, R.layout.date_row, R.id.date,lists);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

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
            Intent intent = new Intent(CourseActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(CourseActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(CourseActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(CourseActivity.this,PersonGroupActivity.class);
            intent.putExtra("AddNewPersonGroup",true);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(CourseActivity.this,MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(CourseActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
