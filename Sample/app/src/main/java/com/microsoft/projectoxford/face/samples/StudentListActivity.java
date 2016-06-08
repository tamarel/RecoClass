package com.microsoft.projectoxford.face.samples;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.helper.SelectImageActivity;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CourseProperties;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListAdapter;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListStudent;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StudentListActivity extends ActionBarActivity {
    ArrayList<String> studentList;
    private CustomListStudent listAdapter;
    private ListView list;
    Button editName;
    String courseId ="", userName="",courseName="";
    Button addStudent,saveButton;
    TextView name,id;
    private List<StudentProperties> students ;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String formattedDate = df.format(c.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            studentList = bundle.getStringArrayList("studentList");
            courseId = bundle.getString("courseID");
            courseName = bundle.getString("courseName");
            userName = bundle.getString("userName");
        }
        setTitle(formattedDate);

        addStudent= (Button)findViewById(R.id.add_student);
        saveButton= (Button)findViewById(R.id.save);
       list = (ListView)findViewById(R.id.list);

        students = new ArrayList<>();
        for(String student : studentList){
            students.add(new StudentProperties(student,"2033"));
        }

        listAdapter = new CustomListStudent(this,
                R.layout.student_row, students);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list.setAdapter(listAdapter);
        final Context context = this;
        // add button listener

        saveButton.setOnClickListener(new View.OnClickListener() {

                                          @Override
                                          public void onClick(View arg0) {


                                              StorageHelper.saveList(userName, courseName, courseId, students, formattedDate);

                                              Intent intent = new Intent(StudentListActivity.this, CourseActivity.class);
                                              intent.putExtra("courseName", courseName);
                                              intent.putExtra("courseId",courseId);
                                              intent.putExtra("userName", userName);
                                              startActivity(intent);
                                          }
                                      });

                addStudent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_student_prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

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
                                        String name = StorageHelper.checIfStudenExistInCourse(StudentListActivity.this, courseId, userInput.getText().toString());
                                       if (name!=null){
                                           students.add(new StudentProperties(name,userInput.getText().toString()));
                                       }
                                        else Toast.makeText(getApplicationContext(),"sorry this ID isn't exist",Toast.LENGTH_SHORT).show();

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

}
