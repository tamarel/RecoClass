package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.AboutUsActivity;
import com.microsoft.projectoxford.face.samples.R;

public class MenuActivity extends ActionBarActivity {
    public GridView grid;
    String userName1;

    public int[] mThumbIds = {
            R.drawable.add_list_button, R.drawable.add_course_button,
            R.drawable.settings_icon, R.drawable.calendar_button,
            R.drawable.add_student_button,R.drawable.about_us_button
    };

    public String[] names = {
            "Courses", "Add course",
            "Settings", "Calendar",
            "Add student","About us" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        CustomGrid adapter = new CustomGrid(MenuActivity.this, names, mThumbIds);
        setTitle("Menu");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName1 = bundle.getString("userName");
        }        grid=(GridView)findViewById(R.id.gridview);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch(position){
                    case 0:
                        Intent intent= new Intent(MenuActivity.this,CoursesActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 1:
                        intent= new Intent(MenuActivity.this,PersonGroupActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 2:
                        intent= new Intent(MenuActivity.this,SettingsActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 3:
                        intent= new Intent(MenuActivity.this,SettingsActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 4:
                        intent= new Intent(MenuActivity.this,PersonActivity.class);
                        intent.putExtra("userName", userName1);
                        startActivity(intent);
                        break;
                    case 5:
                        intent= new Intent(MenuActivity.this,AboutUsActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent= new Intent(MenuActivity.this,AboutUsActivity.class);
                        startActivity(intent);
                        break;



                }
            }
        });


    }






}
