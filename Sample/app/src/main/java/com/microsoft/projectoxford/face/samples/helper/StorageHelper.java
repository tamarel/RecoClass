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
package com.microsoft.projectoxford.face.samples.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.rest.WebServiceRequest;

import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.common.RequestMethod;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.microsoft.projectoxford.face.rest.WebServiceRequest;
import com.microsoft.projectoxford.face.samples.R;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.AddFaceToPersonActivity;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Defined several functions to manage local storage.
 */

public class StorageHelper {




        // with parse get all course by lecture
    public static ArrayList<String> getAllPersonGroupIdsByUserName(Context context,String lectureName) {
        ArrayList<String> listOfCourse=new ArrayList<>();
       ArrayList<ParseObject> listCourse=new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("lectureName", lectureName);
        try{
            listCourse =( ArrayList<ParseObject>)(query.find());
        }
        catch (com.parse.ParseException e){

        }

    for (ParseObject course :listCourse) {

        listOfCourse.add(course.get("CourseName").toString());
    }
        return listOfCourse;
    }




    //with parse add new course
    public static void setGroupName(final String personGroupIdToAdd,  String lectureName, String personGroupName, Context context) {
        final String personGroupName1=personGroupName;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("lectureName", lectureName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> GroupList, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject nameCourse : GroupList) {

                        if (personGroupName1.equals(nameCourse.getString("CourseName")))
                            return;
                    }
                }
            }
        });

             ParseObject groupName = new ParseObject("Course");
             groupName.put("CourseName",personGroupName1);
             groupName.put("lectureName", lectureName);
             groupName.put("studentList", "{}");
             groupName.saveInBackground();



    }



    // with parse
    public static void deletePersonGroups(List<String> personGroupIdsToDelete,String courseName, Context context) {

        ArrayList<ParseObject> objects = new ArrayList<>();
        int i = 0;
        for (String personGroupId : personGroupIdsToDelete) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
            query.whereEqualTo("CourseName", personGroupIdsToDelete.get(i));

            query.findInBackground(new FindCallback<ParseObject>() {
                                       @Override
                                       public void done(List<ParseObject> list, com.parse.ParseException e) {
                                           if (e == null) {


                                               for (ParseObject delete : list) {
                                                   delete.deleteInBackground();

                                               }
                                           } else {

                                           }
                                       }
                                   });
        }


        ArrayList<String> personGroupIds = getAllPersonGroupIdsByUserName(context, ParseUser.getCurrentUser().get("username")
                .toString());
        ArrayList<String> newPersonGroupIds = new ArrayList<>();

        for (String personGroupId : personGroupIds) {
            if (!personGroupIdsToDelete.contains(personGroupId)) {
                newPersonGroupIds.add(personGroupId);


            }
        }
    }



    //create new student
    public static CreatePersonResult createPerson(String personName,String cv, String personGroupId, Context context,String userData){

        WebServiceRequest mRestCall = new WebServiceRequest("1f858fa69d634871bc1aac03e4783ec7");
        String mServiceHost = "https://api.projectoxford.ai/face/v1.0";
        Gson mGson = (new GsonBuilder()).setDateFormat("M/d/yyyy h:m:s a").create();

        ParseObject Student = new ParseObject("Student");
        Student.put("studentName", personName);
        Student.put("CV",cv);
        Student.put("faces", "{}");
        Student.saveInBackground();
        updateCourse(personGroupId,cv,personName,context);

        /*
        HashMap params = new HashMap();
        String uri = String.format("%s/%s/%s/%s", new Object[]{mServiceHost, "persongroups", personGroupId, "persons"});
        params.put("name", personName);
        if(userData != null) {
            params.put("userData", userData);
        }

        String json = null;
        try {
            json = (String)mRestCall.request(uri, RequestMethod.POST, params, (String)null);
            return (CreatePersonResult)mGson.fromJson(json, CreatePersonResult.class);

        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
        return null;
    }

    public static void updateCourse(String course, String cv ,String name,Context context){

        String studentList="";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseName", course);
        JSONArray arr;
        try{
            ParseObject courseResult=((ParseObject)query.getFirst());
            studentList=courseResult.getString("studentList");
            if (!studentList.equals("{}")) {
                arr = new JSONArray(studentList);
            }
            else{
              arr = new JSONArray();
            }
            JSONObject pnObj = new JSONObject();

            pnObj.put("cv", cv );
            pnObj.put("name", name );
            arr.put(pnObj);
            courseResult.put("studentList",arr.toString());
            courseResult.saveInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (com.parse.ParseException e){

        }


    }


    //add person with parse
    public static void setPersonNameWithParse(String personIdToAdd, String personName, String personGroupId, Context context){


    }
    public static void setPersonName(String personIdToAdd, String personName, String personGroupId, Context context) {

        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        personIdNameMapEditor.putString(personIdToAdd, personName);
        personIdNameMapEditor.commit();

        ArrayList<String> personIds = getAllPersonGroupIdsByUserName(context, personGroupId);
        Set<String> newPersonIds = new HashSet<>();
        for (String personId: personIds) {
            newPersonIds.add(personId);
        }
        newPersonIds.add(personIdToAdd);
        SharedPreferences personIdSet =
                context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdSetEditor = personIdSet.edit();
        personIdSetEditor.putStringSet("PersonIdSet", newPersonIds);
        personIdSetEditor.commit();
    }

    public static void deletePersons(List<String> personIdsToDelete, String personGroupId, Context context) {
        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        for (String personId: personIdsToDelete) {
            personIdNameMapEditor.remove(personId);
        }
        personIdNameMapEditor.commit();

        ArrayList<String> personIds = getAllPersonGroupIdsByUserName(context, personGroupId);
        Set<String> newPersonIds = new HashSet<>();
        for (String personId: personIds) {
            if (!personIdsToDelete.contains(personId)) {
                newPersonIds.add(personId);
            }
        }
        SharedPreferences personIdSet =
                context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdSetEditor = personIdSet.edit();
        personIdSetEditor.putStringSet("PersonIdSet", newPersonIds);
        personIdSetEditor.commit();
    }

/*
    public static ArrayList<String>getAllFaceName(String personId, Context context){


        ArrayList<String> listOfName=new ArrayList<>();
        ArrayList<ParseObject> results=new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("CV", personId);

        try{
            results =( ArrayList<ParseObject>)query.find();
        }
        catch (com.parse.ParseException e){

        }

        for (ParseObject student :results) {

            listOfCourse.add(course.get("CourseName").toString());
        }
        return listOfCourse;

    }
    */

    public static ArrayList<String>getAllStudentByCourse(String course, Context context) {


        ArrayList<String> listOfStudent = new ArrayList<>();
        ParseObject results;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseName", course);
        String studentList ;
        try {
            results = (ParseObject)(query.getFirst());


            JSONArray arr = new JSONArray( results.getString("studentList") );

            // ArrayList<String> cvList = new ArrayList<>();
            for(int i = 0; i < arr.length(); i++){
                String cv = arr.getJSONObject(i).getString("cv");
                String name = arr.getJSONObject(i).getString("name");
                listOfStudent.add(name);
            }


        } catch (com.parse.ParseException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return listOfStudent;
    }

    public static ArrayList<String> getAllFaceIdsByPersonRect(String personId, Context context){

        ArrayList<String> listOfFace = new ArrayList<>();
        ParseObject result;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("studentName", personId);

        try{
            result =(ParseObject)query.getFirst();

            JSONArray arr = new JSONArray(result.getString("faces"));

            // ArrayList<String> cvList = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                String face = arr.getJSONObject(i).getString("rect");

                listOfFace.add(face);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        catch (com.parse.ParseException e) {

        }
        return listOfFace;

    }


        public static ArrayList<String> getAllFaceIdsByPerson(String personId, Context context){

            ArrayList<String> listOfFace = new ArrayList<>();
            ParseObject result;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
            query.whereEqualTo("studentName", personId);

            try{
                result =(ParseObject)query.getFirst();

              JSONArray arr = new JSONArray(result.getString("faces"));

              // ArrayList<String> cvList = new ArrayList<>();
              for (int i = 0; i < arr.length(); i++) {
                  String face = arr.getJSONObject(i).getString("face");

                  listOfFace.add(face);
              }
          } catch (JSONException e1) {
              e1.printStackTrace();
          }

        catch (com.parse.ParseException e) {

    }
        return listOfFace;

        }
        public static Set<String> getAllFaceIds(String personId, Context context) {

        SharedPreferences faceIdSet =
                context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        return faceIdSet.getStringSet("FaceIdSet", new HashSet<String>());
    }

    public static String getFaceUri(String faceId, Context context) {
        SharedPreferences faceIdUriMap =
                context.getSharedPreferences("FaceIdUriMap", Context.MODE_PRIVATE);
        return faceIdUriMap.getString(faceId, "");
    }



    // with parse
    public static void setFaceUriOnParse(String faceIdToAdd, String faceUri, String personId, Context context) {


    }

    public static AddPersistedFaceResult addPersonFace(String mPersonGroupId,String personId,
                                                       ByteArrayOutputStream url, String userData, FaceRectangle targetFace) {
        WebServiceRequest mRestCall = new WebServiceRequest("1f858fa69d634871bc1aac03e4783ec7");
         String mServiceHost = "https://api.projectoxford.ai/face/v1.0";
        Gson mGson = (new GsonBuilder()).setDateFormat("M/d/yyyy h:m:s a").create();

        HashMap params = new HashMap();
        String path = String.format("%s/%s/%s/%s/%s/%s", new Object[]{mServiceHost,"persongroups", mPersonGroupId, "persons", personId, "persistedfaces"});
        if (userData != null && userData.length() > 0) {
            params.put("userData", userData);
        }

        String uri;
        if (targetFace != null) {
            uri = String.format("%1d,%2d,%3d,%4d", new Object[]{Integer.valueOf(targetFace.left), Integer.valueOf(targetFace.top), Integer.valueOf(targetFace.width), Integer.valueOf(targetFace.height)});
            params.put("targetFace", uri);
        }

        uri = WebServiceRequest.getUrl(path, params);
        params.clear();
        params.put("url", url);

            String json = "";

        String studentList = "";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("studentName", personId);
        JSONArray arr;
        try {
            ParseObject courseResult = ((ParseObject) query.getFirst());
            studentList = courseResult.getString("faces");
            if (!studentList.equals("{}")) {
                arr = new JSONArray(studentList);
            } else {
                arr = new JSONArray();
            }
            JSONObject pnObj = new JSONObject();
            pnObj.put("face", url);
            pnObj.put("rect", targetFace);

            arr.put(pnObj);
            courseResult.put("faces", arr.toString());
            courseResult.saveInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {

        }
        return (AddPersistedFaceResult) mGson.fromJson(json, AddPersistedFaceResult.class);

    }





    public static void setFaceUri(String faceIdToAdd, String faceUri, String personId, Context context) {
        SharedPreferences faceIdUriMap =
                context.getSharedPreferences("FaceIdUriMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor faceIdUriMapEditor = faceIdUriMap.edit();
        faceIdUriMapEditor.putString(faceIdToAdd, faceUri);
        faceIdUriMapEditor.commit();

        Set<String> faceIds = getAllFaceIds(personId, context);
        Set<String> newFaceIds = new HashSet<>();
        for (String faceId: faceIds) {
            newFaceIds.add(faceId);
        }
        newFaceIds.add(faceIdToAdd);
        SharedPreferences faceIdSet =
                context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor faceIdSetEditor = faceIdSet.edit();
        faceIdSetEditor.putStringSet("FaceIdSet", newFaceIds);
        faceIdSetEditor.commit();
    }

    public static void deleteFaces(List<String> faceIdsToDelete, String personId, Context context) {
        Set<String> faceIds = getAllFaceIds(personId, context);
        Set<String> newFaceIds = new HashSet<>();
        for (String faceId: faceIds) {
            if (!faceIdsToDelete.contains(faceId)) {
                newFaceIds.add(faceId);
            }
        }
        SharedPreferences faceIdSet =
                context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor faceIdSetEditor = faceIdSet.edit();
        faceIdSetEditor.putStringSet("FaceIdSet", newFaceIds);
        faceIdSetEditor.commit();
    }
}
