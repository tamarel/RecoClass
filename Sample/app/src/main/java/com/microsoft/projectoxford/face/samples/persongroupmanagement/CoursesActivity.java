package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

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

import com.microsoft.projectoxford.face.samples.CourseActivity;
import com.microsoft.projectoxford.face.samples.IdentificationActivity;
import com.microsoft.projectoxford.face.samples.LoginActivity;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoursesActivity extends ActionBarActivity {

    private List<CourseProperties> courseList;
    private CustomListAdapter listAdapter;
    private ListView list;
    String userName = "";
    private TextView lectureName ;
    private TextView explain ;
    private ImageButton addCourse;
    private CourseProperties course ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        addCourse = (ImageButton)findViewById(R.id.addButton);
        explain = (TextView)findViewById(R.id.explain);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName = bundle.getString("userName");
           }
        setTitle("Hello " + userName);

        explain.setText("choose a course please\n");
        list = (ListView)findViewById(R.id.list);

        courseList = new ArrayList<>();
        courseList =  StorageHelper.getAllCourseNameByUserName(CoursesActivity.this, userName);

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
                CourseProperties c  = (CourseProperties) parent.getItemAtPosition(index);
                Intent intent = new Intent(CoursesActivity.this, CourseActivity.class);
                intent.putExtra("courseName", c.getCourseId());
                intent.putExtra("courseId",c.getCourseName());
                intent.putExtra("userName", userName);

                startActivity(intent);
                finish();

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {


                String str = list.getItemAtPosition(index).toString();
                Intent intent = new Intent(CoursesActivity.this, CourseActivity.class);
                intent.putExtra("courseName",str);
                startActivity(intent);
                finish();
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

        list.setAdapter(listAdapter);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }


}
