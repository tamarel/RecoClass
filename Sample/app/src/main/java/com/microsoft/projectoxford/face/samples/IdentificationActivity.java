//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Project Oxford: http://ProjectOxford.ai
//
// ProjectOxford SDK Github:
// https://github.com/Microsoft/ProjectOxfordSDK-Windows
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.samples.helper.ImageHelper;
import com.microsoft.projectoxford.face.samples.helper.LogHelper;
import com.microsoft.projectoxford.face.samples.helper.SampleApp;
import com.microsoft.projectoxford.face.samples.helper.SelectImageActivity;
import com.microsoft.projectoxford.face.samples.helper.StorageHelper;
import com.microsoft.projectoxford.face.samples.log.IdentificationLogActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.MenuActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.PersonGroupListActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.SettingsActivity;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class IdentificationActivity extends ActionBarActivity  {
    View _convertView;
    public String mPersonGroupId,code;
     private boolean mSucceed = true;
    ListView listView ;
    public ViewHolder holder;
    public   ArrayList<String> names;
    public int len;
    public String courseName;
    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {

        IdentificationTask(String personGroupId) {
            mPersonGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {
            String logString = "Request: Identifying faces ";
            for (UUID faceId: params) {
                logString += faceId.toString() + ", ";
            }
            logString += " in group " + mPersonGroupId;
            addLog(logString);

            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

            try{
                publishProgress("Getting person group status...");

                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(
                        mPersonGroupId);     /* personGroupId */

                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    publishProgress("Person group training status is " + trainingStatus.status);
                    mSucceed = false;
                    return null;
                }

                publishProgress("Identifying...");

                // Start identification.
                return faceServiceClient.identity(
                        mPersonGroupId,   /* personGroupId */
                        params,                  /* faceIds */
                        1);                      /* maxNumOfCandidatesReturned */
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.a
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(IdentifyResult[] result) {
            // Show the result on screen when detection is done.
            setUiAfterIdentification(result, mSucceed);

        }
    }



    boolean detected;

    FaceListAdapter mFaceListAdapter;

    //PersonGroupListAdapter mPersonGroupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPersonGroupId  = bundle.getString("PersonGroupId");
            code  = bundle.getString("codeCourse");
            courseName  = bundle.getString("PersonGroupName");
        }
        listView= (ListView) findViewById(R.id.list_identified_faces);
        detected = false;
        Button viewLogButton =(Button)findViewById(R.id.view_log);
        viewLogButton.setText("view log");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_title));


    }

    @Override
    protected void onResume() {
        super.onResume();
        Button viewLogButton = (Button) findViewById(R.id.view_log);
        viewLogButton.setText("view log");
    }

    private void setUiBeforeBackgroundTask() {
        progressDialog.show();
    }

    // Show the status of background detection task on screen.
    private void setUiDuringBackgroundTask(String progress) {
        progressDialog.setMessage(progress);

        setInfo(progress);
    }

    // Show the result on screen when detection is done.
    private void setUiAfterIdentification(IdentifyResult[] result, boolean succeed) {

        progressDialog.dismiss();


        setAllButtonsEnabledStatus(true);
        setIdentifyButtonEnabledStatus(false);

        if (succeed) {
            // Set the information about the detection result.
            setInfo("Identification is done");

            if (result != null) {
                mFaceListAdapter.setIdentificationResult(result);

                String logString = "Response: Success. ";
                for (IdentifyResult identifyResult: result) {
                    logString += "Face " + identifyResult.faceId.toString() + " is identified as "
                            + (identifyResult.candidates.size() > 0
                                    ? identifyResult.candidates.get(0).personId.toString()
                                    : "Unknown Person")
                            + ". ";
                }
                addLog(logString);
                Button viewLogButton = (Button) findViewById(R.id.view_log);
                viewLogButton.setText("save a list");

                // Show the detailed list of detected faces.

                listView.setAdapter(mFaceListAdapter);

            }
     }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */

                        null);
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background detection task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            progressDialog.dismiss();

            setAllButtonsEnabledStatus(true);

            if (result != null) {
                // Set the adapter of the ListView which contains the details of detected faces.
                mFaceListAdapter = new FaceListAdapter(result);
                ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                listView.setAdapter(mFaceListAdapter);

                if (result.length == 0) {
                    detected = false;
                    setInfo("No faces detected!");
                    return;
                } else {
                    detected = true;
                    setInfo("Click on the \"Identify\" button to identify the faces in image.");
                }
            } else {
                detected = false;
            }
            names = new ArrayList<>(result.length);
            len= result.length;
            for(int i = 0; i<result.length; i++){
                names.add("");
            }
            refreshIdentifyButtonEnabledStatus();
        }

    }

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The image selected to detect.
    private Bitmap mBitmap;
    List<UUID> faceIds = new ArrayList<>();
    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_SELECT_IMAGE:
                if(resultCode == RESULT_OK) {
                    detected = false;

                    // If image is selected successfully, set the image URI and bitmap.
                    Uri imageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(mBitmap);
                    }

                    // Clear the identification result.
                    FaceListAdapter faceListAdapter = new FaceListAdapter(null);
                    ListView listView = (ListView) findViewById(R.id.list_identified_faces);
                    listView.setAdapter(faceListAdapter);

                    // Clear the information panel.
                    setInfo("");

                    // Start detecting in image.
                    detect(mBitmap);
                }
                break;
            default:
                break;
        }
    }

    // Start detecting in image.
    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        setAllButtonsEnabledStatus(false);

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when the "Detect" button is clicked.
    public void identify(View view) {
        // Start detection task only if the image to detect is selected.


        if (detected && mPersonGroupId != null) {
            // Start a background task to identify faces in the image.

            for (Face face:  mFaceListAdapter.faces) {
                faceIds.add(face.faceId);
            }

            setAllButtonsEnabledStatus(false);

            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
        } else {
            // Not detected or person group exists.
            setInfo("Please select an image first.");
        }
    }

    public void managePersonGroups(View view) {
        Intent intent = new Intent(this, PersonGroupListActivity.class);
        startActivity(intent);

        refreshIdentifyButtonEnabledStatus();
    }

    public void viewLog(View view) {

        Button viewLogButton = (Button) findViewById(R.id.view_log);

       if (viewLogButton.getText().equals("save a list"))
       {
            for(String name: names){
                if (name.equals("select name")) {
                    Toast.makeText(IdentificationActivity.this,"you must enter names of students",Toast.LENGTH_LONG).show();
                    return;
                }
            }

           Intent okIntent = new Intent(IdentificationActivity.this, StudentListActivity.class);
           okIntent.putStringArrayListExtra("studentList", names);
           okIntent.putExtra("courseId", mPersonGroupId);
           okIntent.putExtra("courseName", courseName);
           okIntent.putExtra("codeCourse", code);
           okIntent.putExtra("userName",ParseUser.getCurrentUser().getUsername());


           startActivity(okIntent);


           return;
       }
        Intent intent = new Intent(this, IdentificationLogActivity.class);
        startActivity(intent);
    }

    // Add a log item.
    private void addLog(String log) {
        LogHelper.addIdentificationLog(log);
    }

    // Set whether the buttons are enabled.
    private void setAllButtonsEnabledStatus(boolean isEnabled) {



        Button groupButton = (Button) findViewById(R.id.select_image);
        groupButton.setEnabled(isEnabled);

        Button identifyButton = (Button) findViewById(R.id.identify);
        identifyButton.setEnabled(isEnabled);

        Button viewLogButton = (Button) findViewById(R.id.view_log);
        viewLogButton.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void setIdentifyButtonEnabledStatus(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.identify);
        button.setEnabled(isEnabled);
    }

    // Set the group button is enabled or not.
    private void refreshIdentifyButtonEnabledStatus() {
        if (detected && mPersonGroupId != null) {
            setIdentifyButtonEnabledStatus(true);
        } else {
            setIdentifyButtonEnabledStatus(false);
        }
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    public ArrayList mData = new ArrayList();

    // The adapter of the GridView which contains the details of the detected faces.
    private class FaceListAdapter extends BaseAdapter  {
        // The detected faces.
        List<Face> faces;

        List<IdentifyResult> mIdentifyResults;

        // The thumbnails of detected faces.
        List<Bitmap> faceThumbnails;

        // Initialize with detection result.
        FaceListAdapter(Face[] detectionResult) {

            faces = new ArrayList<>();
            faceThumbnails = new ArrayList<>();
            mIdentifyResults = new ArrayList<>();

            if (detectionResult != null) {
                faces = Arrays.asList(detectionResult);
                Toast.makeText(IdentificationActivity.this,""+faces.size(),Toast.LENGTH_LONG).show();
                for (Face face: faces) {
                    try {
                        // Crop face thumbnail with five main landmarks drawn from original image.
                        faceThumbnails.add(ImageHelper.generateFaceThumbnail(
                                mBitmap, face.faceRectangle));
                    } catch (IOException e) {
                        // Show the exception when generating face thumbnail fails.
                        setInfo(e.getMessage());
                    }
                }
            }
        }

        public void setIdentificationResult(IdentifyResult[] identifyResults) {
            mIdentifyResults = Arrays.asList(identifyResults);
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return faces.size();
        }

        @Override
        public Object getItem(int position) {
            return faces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }




        ArrayAdapter<String> adapter;


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            LayoutInflater layoutInflater =
                    (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final ViewHolder holder;


            if (convertView == null) {
                holder = new ViewHolder();

                convertView = layoutInflater.inflate(
                        R.layout.item_face_with_description, parent, false);
                holder.txt = (TextView)convertView.findViewById(R.id.text_detected_face);
                holder.txt.setTag(position);
                holder.spinner =(Spinner)convertView.findViewById(R.id.add_student_to_face);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
                if (names.get(position)!= null)
                    holder.txt.setText(names.get(position));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {


                    if (parent.getItemAtPosition(pos).equals("select name"))
                        return;
                    else {
                        String identity = "" + parent.getItemAtPosition(pos);
                        names.set(position%len, identity);
                        holder.txt.setText(identity);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            // Show the face thumbnail.
            ((ImageView)convertView.findViewById(R.id.face_thumbnail)).setImageBitmap(
                    faceThumbnails.get(position));
            holder.spinner.setVisibility(View.INVISIBLE);

            if (mIdentifyResults.size() == faces.size()) {
                // Show the face details.

                if (mIdentifyResults.get(position).candidates.size() > 0) {

                    String personId =
                            mIdentifyResults.get(position).candidates.get(0).personId.toString();
                    String personName = StorageHelper.getPersonName(
                            personId, mPersonGroupId, IdentificationActivity.this);

                    String identity = "" + personName;
                    String cv = StorageHelper.getIdOfStudent(personId);
                    names.set(position, identity + "," + cv);

                    holder.txt.setText(identity+","+cv);
                    holder.txt.setTag(position);
                    notifyDataSetChanged();


                } else {





                    ArrayList<String> arraySpinner = StorageHelper.getAllStudentByCourse(mPersonGroupId,IdentificationActivity.this);
                    List<String> list = new ArrayList<String>(arraySpinner);
                    list.add(0,"select name");


                    Spinner studentInClass = (Spinner) findViewById(R.id.add_student_to_face);

                    adapter =  new ArrayAdapter<String>(IdentificationActivity.this,android.R.layout.simple_spinner_item,list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                    ((Spinner) convertView.findViewById(R.id.add_student_to_face)).setAdapter(adapter);
                    ((Spinner) convertView.findViewById(R.id.add_student_to_face)).setVisibility(View.VISIBLE);

                    holder.spinner.setVisibility(View.VISIBLE);


                }


            }

            return convertView;
        }

    }

    public  static class ViewHolder {
        public TextView txt;
        public Spinner spinner;

        int index;

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
            Intent intent = new Intent(IdentificationActivity.this,MainActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_aboutUs){
            Intent intent = new Intent(IdentificationActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_calendar){
            Intent intent = new Intent(IdentificationActivity.this,AboutUsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_addCourse){
            Intent intent = new Intent(IdentificationActivity.this,PersonGroupActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            intent.putExtra("AddNewPersonGroup",true);
            String personGroupId = UUID.randomUUID().toString();
            intent.putExtra("PersonGroupName", "");
            intent.putExtra("PersonGroupId", personGroupId);
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_goMenu){

            Intent intent = new Intent(IdentificationActivity.this,MenuActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }
        else if ( id == R.id.menu_settings){
            Intent intent = new Intent(IdentificationActivity.this,SettingsActivity.class);
            intent.putExtra("userName", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
