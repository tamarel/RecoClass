package com.microsoft.projectoxford.face.samples;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.helper.DB;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

// this class responsibility about query

public class QueriesActivity extends ActionBarActivity {

    private Calendar calendar;
    private TextView dateViewTo,dateViewFrom;
    private int year, month, day;
    private Button fromButton,toButton,submit;
    private int buttonClick;
    private Spinner spinnerAction,action2;
    private EditText number;
    private String courseId,code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            courseId = bundle.getString("PersonGroupId");
            code = bundle.getString("codeCourse");
        }
        setTitle("Query activity");
        //initialize
        dateViewTo = (TextView) findViewById(R.id.toText);
        dateViewFrom = (TextView) findViewById(R.id.fromText);
        calendar = Calendar.getInstance();
        number = (EditText)findViewById(R.id.number);
        year = calendar.get(Calendar.YEAR);
        fromButton = (Button)findViewById(R.id.from);
        toButton = (Button)findViewById(R.id.until);
        submit = (Button)findViewById(R.id.submit);
        buttonClick = 1;
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        spinnerAction = (Spinner) findViewById(R.id.spinnerAction);
        action2 = (Spinner) findViewById(R.id.action);
        String [] action ={"Attendance rate" , "Nonattendance rate"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,action);

    // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
        spinnerAction.setAdapter(adapter);

        String [] act ={"<",">","!=","="};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,act);

        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        action2.setAdapter(adapter2);
        dateViewFrom.setText("");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Log.i("info", dateViewFrom.getText().toString().equals("")+"");
                    if(dateViewFrom.getText().toString().equals("")){
                        Toast.makeText(QueriesActivity.this,"You must enter date",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Date to = sdf.parse(dateViewTo.getText().toString());
                    Date from = sdf.parse(dateViewFrom.getText().toString());
                    if (from.after(to)) {
                        Toast.makeText(QueriesActivity.this,"Error in dates",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(number.getText().toString().equals("")){
                        Toast.makeText(QueriesActivity.this,"You must enter number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Intent intent   = new Intent(QueriesActivity.this,QueryListActivity.class);
                        intent.putExtra("studentList", DB.runQuery(dateViewFrom.getText().toString(),
                                dateViewTo.getText().toString(), courseId, spinnerAction.getSelectedItem().toString(),
                                action2.getSelectedItem().toString(), Integer.parseInt(number.getText().toString()), QueriesActivity.this));

                        startActivity(intent);
                    }
                }
                catch (ParseException e){}

            }


        });

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick = 0;
                setDate(v);
            }


        });



        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick = 1;
                setDate(v);
            }


        });
    }




    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {

        if ( buttonClick == 1)
        {
            dateViewTo.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
        else {
            dateViewFrom.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        }
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
            Intent intent = new Intent(QueriesActivity.this,MainActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(QueriesActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(QueriesActivity.this,CalendarActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(QueriesActivity.this,PersonGroupActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(QueriesActivity.this,MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(QueriesActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
