package com.microsoft.projectoxford.face.samples;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CourseProperties;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListAdapter;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.DataList;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseActivity extends ActionBarActivity {

    private ArrayList<String> lists;
    private ArrayAdapter listAdapter;
    private ListView list;
    private DataList data;
    private ImageButton addList;
    private TextView explain ;
    private String courseId;
    private String courseName,userName="";
    private Button manageCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseName = bundle.getString("courseName");
            courseId =bundle.getString("courseId");
            userName =bundle.getString("userName");
        }
        list = (ListView)findViewById(R.id.list);
        addList =(ImageButton)findViewById(R.id.addButton);

        lists = StorageHelper.getAllListAttendance(userName,courseName,StorageHelper.getCourseId(courseName, ParseUser.getCurrentUser().getUsername()));

            listAdapter = new ArrayAdapter(this, R.layout.date_row, R.id.date,lists);
            listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            list.setAdapter(listAdapter);

        explain = (TextView)findViewById(R.id.explain);
        manageCourse = (Button)findViewById(R.id.manageCourse);
        explain.setText("choose a list or add a new list");


        addList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), IdentificationActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("PersonGroupId", StorageHelper.getCourseId(courseName, ParseUser.getCurrentUser().getUsername()));
                startActivity(intent);

            }
        });
        manageCourse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PersonGroupActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("PersonGroupId", StorageHelper.getCourseId(courseName, ParseUser.getCurrentUser().getUsername()) );
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
                Intent intent = new Intent(CourseActivity.this, StudentListActivity.class);
                
                intent.putExtra("courseName", c.getCourseId());
                intent.putExtra("courseId", c.getCourseName());
                intent.putExtra("userName", userName);

                startActivity(intent);
                finish();

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
        lists = StorageHelper.getAllListAttendance(userName,courseName,StorageHelper.getCourseId(courseName, ParseUser.getCurrentUser().getUsername()));
        listAdapter = new ArrayAdapter(this, R.layout.date_row, R.id.date,lists);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

    }

}
