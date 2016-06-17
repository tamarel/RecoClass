package com.microsoft.projectoxford.face.samples;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.QueryRow;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//this class show the result of identification.

public class StudentListActivity extends ActionBarActivity {
    ArrayList<String> studentList;
    private CustomListStudent listAdapter;
    private ListView list;
    Button editName;
    String courseId = "", userName = "", courseName = "",code="";
    Button addStudent, saveButton, exportButton, add_picture;
    TextView name, id;
    String date;
    private List<StudentProperties> students;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SharedPreferences sharedpreferences;
    public String currentDate;
    String formattedDate = sdf.format(c.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        Bundle bundle = getIntent().getExtras();
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (bundle != null) {
            studentList = bundle.getStringArrayList("studentList");
            courseId = bundle.getString("courseId");
            courseName = bundle.getString("courseName");
            userName = bundle.getString("userName");
            date = bundle.getString("Date");
            if (date!=null)
                currentDate = date;
            else currentDate=formattedDate;
            code = bundle.getString("codeCourse");
        }
        //set title to date
        setTitle(formattedDate);

        setTitle(formattedDate);

        addStudent = (Button) findViewById(R.id.add_student);
        saveButton = (Button) findViewById(R.id.save);
        add_picture = (Button) findViewById(R.id.add_picture);
        exportButton = (Button) findViewById(R.id.export);
        list = (ListView) findViewById(R.id.list);

        students = new ArrayList<>();
        Set<String> set = sharedpreferences.getStringSet("key", null);
        if (set != null) {
            for (String student : set) {
                String[] s = student.split(",");
                students.add(new StudentProperties(s[0], s[1]));
            }
        }
        for (String student : studentList) {
            String[] s = student.split(",");
            students.add(new StudentProperties(s[0], s[1]));

        }

        //list adapter
        listAdapter = new CustomListStudent(this,
                R.layout.student_row, students);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list.setAdapter(listAdapter);
        final Context context = this;

        //on click
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           final int index1, long arg3) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudentListActivity.this);
                alertDialogBuilder.setMessage("Do you want to remove this row?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        students.remove(index1);
                        listAdapter = new CustomListStudent(StudentListActivity.this,
                                R.layout.student_row, students);
                        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


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
        // add button listener
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                StorageHelper.saveList(userName, courseName, courseId, students, currentDate);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putStringSet("key", null);
                editor.commit();// commit is important here.

                Intent intent = new Intent(StudentListActivity.this, CourseActivity.class);
                intent.putExtra("courseName", courseName);
                intent.putExtra("courseCode", code);
                intent.putExtra("courseId", courseId);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        add_picture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Set<String> set = new HashSet<String>();
                for( StudentProperties student: students) {
                    set.add(student.getName()+","+student.getId());
                }
                editor.putStringSet("key", set);
                editor.commit();
                Intent intent = new Intent(StudentListActivity.this, IdentificationActivity.class);
                intent.putExtra("PersonGroupName", courseName);
                intent.putExtra("PersonGroupId",  courseId);
                intent.putExtra("courseCode",  code);
                startActivity(intent);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Uri u1 = null;
                String sFileName = "ListOfAttendance" + ".csv";
                try {
                    File root = Environment.getExternalStorageDirectory();
                    File gpxfile = new File(root, sFileName);
                    FileWriter writer = new FileWriter(gpxfile);


                    writer.append("Student Name");
                    writer.append(',');
                    writer.append("ID");

                    writer.append('\n');

                    for (StudentProperties student : students) {
                        writer.append(student.getName());
                        writer.append(',');
                        writer.append(student.getId());
                        writer.append(',');

                        writer.append('\n');
                    }

                    //generate whatever data you want
                    writer.flush();
                    writer.close();
                    u1 = Uri.fromFile(gpxfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "List of attendance -" + formattedDate);
                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                sendIntent.setType("text/csv");
                startActivity(sendIntent);


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
                                        if (name != null) {
                                            students.add(new StudentProperties(name, userInput.getText().toString()));
                                        } else
                                            Toast.makeText(getApplicationContext(), "sorry this ID isn't exist", Toast.LENGTH_SHORT).show();

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
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.
            Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_aboutUs) {


            Intent intent = new Intent(StudentListActivity.this, AboutUsActivity.class);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_calendar) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.

            Intent intent = new Intent(StudentListActivity.this, CalendarActivity.class);

            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_addCourse) {
            Intent intent = new Intent(StudentListActivity.this, PersonGroupActivity.class);
            intent.putExtra("AddNewPersonGroup", true);
            String personGroupId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_goMenu) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.
            Intent intent = new Intent(StudentListActivity.this, MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_settings) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("key", null);
            editor.commit();// commit is important here.
            Intent intent = new Intent(StudentListActivity.this, SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
