package com.microsoft.projectoxford.face.samples;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListQuery;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CustomListStudent;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.QueryRow;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;
import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tamarazu on 6/10/2016.
 * show list result of query
 */

public class QueryListActivity extends Activity{
    TextView name,id,number;
    private List<QueryRow> students ;
    private JSONArray jsonArr;
    ArrayList<String> studentList;
    private ListView list;
    Button addStudent,saveButton;

    private CustomListQuery listAdapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_list);
        Bundle bundle = getIntent().getExtras();
        setTitle("Result");
            try {
                if (bundle != null) {
                jsonArr = new JSONArray(bundle.getString("studentList"));
            }

            students = new ArrayList<>();

             if (jsonArr != null) {
                 for (int i = 0; i < jsonArr.length(); i++) {
                     JSONObject student = jsonArr.getJSONObject(i);
                     students.add(new QueryRow(student.getString("name"), student.getString("id"), student.getInt("number")));

                 }
             }
        }
            catch (JSONException e){e.printStackTrace();}



        list = (ListView)findViewById(R.id.list);

        saveButton= (Button)findViewById(R.id.save);

        listAdapter = new CustomListQuery(this,
                R.layout.row_of_query, students);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        list.setAdapter(listAdapter);

        //export to csv file
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Uri u1  =   null;
                String sFileName = "queryResult.csv";
                try
                {
                    File root = Environment.getExternalStorageDirectory();
                    File gpxfile = new File(root, sFileName);
                    FileWriter writer = new FileWriter(gpxfile);


                    writer.append("Student Name");
                    writer.append(',');
                    writer.append("ID");
                    writer.append(',');
                    writer.append("Number");
                    writer.append('\n');

                    for(QueryRow student: students){
                        writer.append(student.getName());
                        writer.append(',');
                        writer.append(student.getId());
                        writer.append(',');
                        writer.append(student.getNumber()+"");
                        writer.append(',');
                        writer.append('\n');
                    }

                    //generate whatever data you want
                    writer.flush();
                    writer.close();
                    u1  =   Uri.fromFile(gpxfile);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Query result");
                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                sendIntent.setType("text/csv");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_signOut) {
            ParseUser.logOutInBackground();
            Intent intent = new Intent(QueryListActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(QueryListActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(QueryListActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(QueryListActivity.this,PersonGroupActivity.class);
            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){
            Intent intent = new Intent(QueryListActivity.this,Menu.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(QueryListActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
