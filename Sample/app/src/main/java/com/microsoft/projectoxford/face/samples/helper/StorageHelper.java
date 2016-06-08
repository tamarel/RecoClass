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
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import com.microsoft.projectoxford.face.samples.persongroupmanagement.CourseProperties;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Defined several functions to manage local storage.
 */

public class StorageHelper {


    //Tamar's function - get course name by id.
    public static String getCourseName(String personGroupId, Context context){
        ArrayList<ParseObject> listCourse=new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");

        query.whereEqualTo("CourseId", personGroupId);

        try{
            listCourse =( ArrayList<ParseObject>)(query.find());
        }
        catch (com.parse.ParseException e){
        }

        for (ParseObject course :listCourse) {
            return course.get("CourseName").toString();
        }
        return "";
    }
    // Tamar's function - get id by course Name
    public static String getCourseId(String courseName,String lectureName){
        String courseId = "";
        ParseObject result;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("lectureName", lectureName);
        query.whereEqualTo("CourseName", courseName);
        try{
            result = (ParseObject) (query.getFirst());
            courseId =  result.get("CourseId").toString();
        }

        catch (com.parse.ParseException e){
        }

        return courseId;
    }

    //Tamar's function - check if student exist in course
    public static String checIfStudenExistInCourse(Context context, String courseId,String id) {

        String studentList = "";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", courseId);
        JSONArray arr;
        try {
            ParseObject courseResult = ((ParseObject) query.getFirst());
            studentList = courseResult.getString("studentList");
            if (!studentList.equals("{}")) {
                arr = new JSONArray(studentList);
            } else {
                arr = new JSONArray();
            }
            for (int i = 0; i < arr.length(); i++) {
                JSONObject student = arr.getJSONObject(i);
                String cv = student.getString("cv");
                if (cv.equals(id))
                    return student.getString("name");

            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        return null;
    }




    // Tamar's function - get all course by lecture
    public static ArrayList<String> getAllCourseIdsByUserName(Context context,String lectureName) {

        ArrayList<String> listOfCourse=new ArrayList<>();
        ArrayList<ParseObject> listCourse=new ArrayList<>();
        try{
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
            query.whereEqualTo("lectureName", lectureName);
            listCourse =( ArrayList<ParseObject>)(query.find());
        }
        catch (com.parse.ParseException e){
        }

        for (ParseObject course :listCourse) {
            listOfCourse.add(course.get("CourseId").toString());
        }
        return listOfCourse;
    }

    public static ArrayList<CourseProperties> getAllCourseNameByUserName(Context context,String lectureName) {

        ArrayList<String> listOfCourse=getAllCourseIdsByUserName(context,lectureName);
        ArrayList<CourseProperties> listOfCourses= new ArrayList<>();

        for (String course: listOfCourse) {
            listOfCourses.add(new CourseProperties("10058",StorageHelper.getCourseName(course,context)));
        }
        return listOfCourses;
    }



    public static Set<String> getAllPersonGroupIds(Context context) {
        SharedPreferences personGroupIdSet =
                context.getSharedPreferences("PersonGroupIdSet", Context.MODE_PRIVATE);
        return personGroupIdSet.getStringSet("PersonGroupIdSet", new HashSet<String>());
    }

    public static void setPersonGroupName(String personGroupIdToAdd, String personGroupName, Context context) {
        SharedPreferences personGroupIdNameMap =
                context.getSharedPreferences("PersonGroupIdNameMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor personGroupIdNameMapEditor = personGroupIdNameMap.edit();
        personGroupIdNameMapEditor.putString(personGroupIdToAdd, personGroupName);
        personGroupIdNameMapEditor.commit();

        Set<String> personGroupIds = getAllPersonGroupIds(context);
        Set<String> newPersonGroupIds = new HashSet<>();
        for (String personGroupId: personGroupIds) {
            newPersonGroupIds.add(personGroupId);
        }
        newPersonGroupIds.add(personGroupIdToAdd);
        SharedPreferences personGroupIdSet =
                context.getSharedPreferences("PersonGroupIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personGroupIdSetEditor = personGroupIdSet.edit();
        personGroupIdSetEditor.putStringSet("PersonGroupIdSet", newPersonGroupIds);
        personGroupIdSetEditor.commit();
    }

    public static String getPersonName(String personId, String personGroupId, Context context) {

        ArrayList<String> listOfStudent = new ArrayList<>();
        ParseObject result;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);
        String studentName="" ;
        try {
            result = (ParseObject) (query.getFirst());
            studentName =  result.get("studentName").toString();

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
       return studentName;

    }
        public static Set<String> getAllPersonIds(String personGroupId, Context context) {
        SharedPreferences personIdSet =
                context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        return personIdSet.getStringSet("PersonIdSet", new HashSet<String>());
    }

    //Tamar's function - set course name
    public static void setCourseName(final String personGroupIdToAdd,  String lectureName, String courseName, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", personGroupIdToAdd);


        try {
            if (query.count()==0) {


                final String personGroupName1=courseName;
                query = ParseQuery.getQuery("Course");
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
                     groupName.put("CourseName",courseName);
                     groupName.put("CourseId",personGroupIdToAdd);
                     groupName.put("lectureName", ParseUser.getCurrentUser().get("username"));
                     groupName.put("studentList", "{}");
                     groupName.saveInBackground();
                }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }


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


        ArrayList<String> personGroupIds = getAllCourseIdsByUserName(context, ParseUser.getCurrentUser().get("username")
                .toString());
        ArrayList<String> newPersonGroupIds = new ArrayList<>();

        for (String personGroupId : personGroupIds) {
            if (!personGroupIdsToDelete.contains(personGroupId)) {
                newPersonGroupIds.add(personGroupId);


            }
        }
    }


    public static String getPersonId(String StudentName, String CV){


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("studentName", StudentName);
        //need to add check on cv
        String StudentId="";
        try{
            ParseObject courseResult=((ParseObject)query.getFirst());
            StudentId=courseResult.getString("personId");
          }
        catch (com.parse.ParseException e){

        }
        return StudentId;
    }
    //Tamar's function - create a new student
    public static CreatePersonResult createPerson(String personName,String cv,String personId, String personGroupId, Context context,String userData) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);

        try {
            if (query.count()==0) {

                ParseObject Student = new ParseObject("Student");
                Student.put("studentName", personName);
                Student.put("CV", cv);
                Student.put("faces", "{}");
                Student.put("personId", personId);
                Student.saveInBackground();
                updateCourse(personGroupId, cv, personName, context);

                return null;
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateCourse(String courseId, String cv ,String name,Context context){

        String studentList="";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", courseId);
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

        ArrayList<String> personIds = getAllCourseIdsByUserName(context, personGroupId);
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

        ArrayList<String> personIds = getAllCourseIdsByUserName(context, personGroupId);
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

    //Tamar's function - get all students of course.
    public static ArrayList<String>getAllStudentByCourse(String courseId, Context context) {


        ArrayList<String> listOfStudent = new ArrayList<>();
        ParseObject results;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", courseId);
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
            e.printStackTrace();
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

    public static Set<String> getAllFaceIdsByStudentName(String personName, Context context) {
        Set<String> listOfFace = new HashSet<>();

        ParseObject result;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("studentName", personName);

        try{
            result =(ParseObject)query.getFirst();

            JSONArray arr = new JSONArray(result.getString("faces"));

            // ArrayList<String> cvList = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                String face = arr.getJSONObject(i).getString("faceId");

                listOfFace.add(face);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        catch (com.parse.ParseException e) {

        }
        return listOfFace;

    }

    //tamar's function
        public static Set<String> getAllFaceIdsByStudent(String personId, Context context){

            Set<String> listOfFace = new HashSet<>();

            ParseObject result;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
            query.whereEqualTo("personId", personId);

            try{
                result =(ParseObject)query.getFirst();

              JSONArray arr = new JSONArray(result.getString("faces"));

              // ArrayList<String> cvList = new ArrayList<>();
              for (int i = 0; i < arr.length(); i++) {
                  String face = arr.getJSONObject(i).getString("faceId");

                  listOfFace.add(face);
              }
          } catch (JSONException e1) {
              e1.printStackTrace();
          }

        catch (com.parse.ParseException e) {

    }
        return listOfFace;

        }
       /* public static Set<String> getAllFaceIds(String personId, Context context) {

        SharedPreferences faceIdSet =
                context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        return faceIdSet.getStringSet("FaceIdSet", new HashSet<String>());
    }
*/


    public static String getFaceUriFromParse(String faceId,String StudentId, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", StudentId);
        //need to add check on cv
        String facesUri="";
        ParseObject results;

        try{

            results = (ParseObject)(query.getFirst());
            JSONArray arr = new JSONArray(results.getString("faces") );

            // ArrayList<String> cvList = new ArrayList<>();
            for(int i = 0; i < arr.length(); i++) {
                String id= arr.getJSONObject(i).getString("faceId");
                if (faceId.equals(id)) {
                    facesUri=arr.getJSONObject(i).getString("faceUri");
                    return (arr.getJSONObject(i).getString("faceUri"));
                }
            }
        }
        catch (com.parse.ParseException e){

        }
        catch (JSONException e){

        }
        return facesUri;
    }

    public static ArrayList getAllListAttendance(String lectureName, String course, String code )
    {
        ArrayList<ParseObject> lisetOfAttendace = new ArrayList();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.whereEqualTo("LectureName", lectureName);
        query.whereEqualTo("Course", course);
        query.whereEqualTo("Code", code);
        JSONArray arr;
        String studentList="";
        try {
            lisetOfAttendace =( ArrayList<ParseObject>)(query.find());

        }
        catch (com.parse.ParseException e){
            e.printStackTrace();
        }
        ArrayList<String> listDates = new ArrayList<>() ;
        for (ParseObject date : lisetOfAttendace) {
            Object s = date.get("Date");
            if (s!=null) {
                listDates.add(date.get("Date").toString());
            }
        }

        return listDates;
    }

    public static void saveList(String lectureName, String course, String code, List<StudentProperties> studentList, String date){


        ParseObject list = new ParseObject("List");
        list.put("LectureName", lectureName);
        list.put("Course", course);
        list.put("Code", code);
        list.put("Date",date);
        JSONArray arr = new JSONArray();
        try{

        for (StudentProperties s : studentList ) {
            JSONObject pnObj = new JSONObject();

            pnObj.put("cv", s.getId() );
            pnObj.put("name", s.getName() );
            arr.put(pnObj);

        }
        list.put("List",arr);
        list.saveInBackground();



        } catch (JSONException e) {
            e.printStackTrace();
     }
    }


    //Tamar's function - set face uri per student
    public static void saveFaceUri(String faceIdToAdd, String faceUri,String cv,String personId,String courseId ,String studentName,Context context){

        Set<String> faceIds = getAllFaceIdsByStudent(personId, context);
        Set<String> newFaceIds = new HashSet<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);
        JSONArray arr;
        String studentList="";
        try {
            if (query.count()==0 )
            {
                createPerson(studentName,cv,personId,courseId,context,ParseUser.getCurrentUser().get("username").toString() );
                query = ParseQuery.getQuery("Student");
                query.whereEqualTo("personId", personId);
            }
            ParseObject courseResult = ((ParseObject) query.getFirst());
            studentList = courseResult.getString("faces");

            if (!studentList.equals("{}")) {
                arr = new JSONArray(studentList);
            } else {
                arr = new JSONArray();
            }
            JSONObject pnObj = new JSONObject();
            pnObj.put("faceId", faceIdToAdd);
            pnObj.put("faceUri", faceUri);

            arr.put(pnObj);
            courseResult.put("faces", arr.toString());
            courseResult.saveInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {

        }

    }
    public static void setFaceUri(String faceIdToAdd, String faceUri, String personId, Context context) {
        SharedPreferences faceIdUriMap =
                context.getSharedPreferences("FaceIdUriMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor faceIdUriMapEditor = faceIdUriMap.edit();
        faceIdUriMapEditor.putString(faceIdToAdd, faceUri);
        faceIdUriMapEditor.commit();

        Set<String> faceIds = getAllFaceIdsByStudent(personId, context);
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
        Set<String> faceIds = getAllFaceIdsByStudent(personId, context);
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
