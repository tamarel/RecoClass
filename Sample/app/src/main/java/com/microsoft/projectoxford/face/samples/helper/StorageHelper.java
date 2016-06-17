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
import android.net.Uri;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
            return course.get("CourseName").toString()+","+course.get("Code");
        }
        return "";
    }
    // Tamar's function - get id by course Name
    public static String getCourseId(String courseName,String lectureName,String code){
        String courseCode = "";
        ParseObject result;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");

        query.whereEqualTo("lectureName", lectureName);
        query.whereEqualTo("Code", code);
        query.whereEqualTo("CourseName", courseName);
        try{
            result = (ParseObject) (query.getFirst());
            courseCode =  result.get("CourseId").toString();
        }

        catch (com.parse.ParseException e){
        }

        return courseCode;
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
            String [] propeties = StorageHelper.getCourseName(course,context).split(",");
            listOfCourses.add(new CourseProperties(propeties[0],propeties[1]));
        }
        return listOfCourses;
    }




    public static String getPersonName(String personId, String personGroupId, Context context) {

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


    //Tamar's function - set course name
    public static boolean setCourseName(final String personGroupIdToAdd, String code ,String lectureName, String courseName, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("Code", code);
        query.whereNotEqualTo("CourseId", personGroupIdToAdd);

        try{
            if (query.count()!=0)
                return false;
            query = ParseQuery.getQuery("Course");

            query.whereEqualTo("CourseId", personGroupIdToAdd);

            if (query.count()==0) {



                ParseObject groupName = new ParseObject("Course");
                groupName.put("CourseName",courseName);
                groupName.put("Code",code);
                groupName.put("CourseId",personGroupIdToAdd);
                groupName.put("lectureName", ParseUser.getCurrentUser().get("username"));
                groupName.put("studentList", "{}");
                groupName.saveInBackground();
                return  true;
            }
            else
            {

                ParseObject courseResult=((ParseObject)query.getFirst());
                courseResult.put("CourseName",courseName);
                courseResult.put("Code",code);

            }


                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                    return false;
                }


        return true;
    }

    public static boolean deleteList(String personGroupId,String date, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.whereEqualTo("Code", personGroupId);
        query.whereEqualTo("Date", date);


        try{
            ParseObject courseResult=((ParseObject)query.getFirst());
            courseResult.deleteInBackground();
        }
        catch (com.parse.ParseException e){
            return false;
        }



        return true;

    }

    public static boolean  deleteAllList(String personGroupIdToDelete, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.whereEqualTo("Code", personGroupIdToDelete);
        ArrayList<ParseObject> lists = new ArrayList<>();
        try{
            lists =( ArrayList<ParseObject>)(query.find());
        }
        catch (com.parse.ParseException e){
            return false;
        }

        for (ParseObject list :lists) {
            list.deleteInBackground();
        }

        return true;

    }
    // with parse
    public static void deletePersonGroups(String personGroupIdToDelete, Context context) {

        ArrayList<ParseObject> objects = new ArrayList<>();
        int i = 0;

            //remove all list attendance -
            deleteAllList(personGroupIdToDelete, context);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
            query.whereEqualTo("CourseId", personGroupIdToDelete);
            try{
                ParseObject courseResult=((ParseObject)query.getFirst());
                courseResult.deleteInBackground();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
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
    public static boolean createPerson(String personName,String cv,String personId, String personGroupId,
                                                  String courseName, String code,Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);

        try {
            if (query.count()==0) {


               if (checIfStudenExistInCourse(context,personGroupId,cv)!=null)
                    return false;
                ParseObject Student = new ParseObject("Student");
                Student.put("studentName", personName);
                Student.put("CV", cv);
                Student.put("faces", "{}");
                Student.put("personId", personId);
                Student.saveInBackground();
                if (!updateCourse(personGroupId, courseName,code,cv, personName, context))
                    return false;
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateCourse(String courseId, String courseName,String courseCode,String cv ,String name,Context context){

        String studentList="";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", courseId);

        JSONArray arr;
        try{
            if ( query.count()==0){
                if (!setCourseName(courseId,courseCode,ParseUser.getCurrentUser().getUsername(),courseName,context))
                    return false;
            }
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
            return false;
        }
        catch (com.parse.ParseException e){

        }

        return true;
    }




    public static boolean deletePerson(String personId,String cv,String personGroupId){

        //delete from student table

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);
        try {
            ParseObject courseResult=((ParseObject)query.getFirst());
            courseResult.deleteInBackground();


        }
        catch (com.parse.ParseException e){
            return false;
        }


        //remove from course table
        // remove from student table.
        String studentList="";
        query = ParseQuery.getQuery("Course");
        query.whereEqualTo("CourseId", personGroupId);
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
           for (int i = 0 ; i<arr.length() ;i++){
               if (arr.getJSONObject(i).getString("cv").equals(cv))
                   arr.remove(i);
           }
            courseResult.put("studentList",arr.toString());
            courseResult.saveInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (com.parse.ParseException e){

        }


        return true;
    }

    public static String getPersonId(String cv){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("CV", cv);
        try {
            ParseObject courseResult=((ParseObject)query.getFirst());

            return courseResult.get("personId").toString();


        }
        catch (com.parse.ParseException e){}
        return "";
    }
    //delete student from course.
    public static void deletePersons(List<String> personIdsToDelete,List<String>  cv ,String personGroupId, Context context) {

        for (int i = 0;i<personIdsToDelete.size() ; i++){
            deletePerson(personIdsToDelete.get(i),cv.get(i), personGroupId);

        }


    }



    public static String getIdOfStudent(String personId){

        ParseObject result;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);


        try{
            result =(ParseObject)query.getFirst();
            return result.get("CV").toString();

            }


        catch (com.parse.ParseException e) {

        }
        return "";

    }

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
                listOfStudent.add(name+","+cv);
            }


        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return listOfStudent;
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



    public static String getFaceUriFromParse(String faceId,String StudentId, Context context) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", StudentId);
        //need to add check on cv
        String facesUri="";
        ParseObject results;
        ParseFile facefile;
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


    public static ArrayList getAllListAttendanceByid (String lectureName, String course, String code , String date ){
        ArrayList<String> listOfStudents =new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.whereEqualTo("LectureName", lectureName);
        query.whereEqualTo("Course", course);
        query.whereEqualTo("Code", code);
        query.whereEqualTo("Date", date);
        JSONArray arr;
        String studentList="";

        try {
            ParseObject courseResult = ((ParseObject) query.getFirst());
            studentList = courseResult.getString("Names");
            if (!studentList.equals("{}")) {
                arr = new JSONArray(studentList);
            } else {
                arr = new JSONArray();
            }
            for (int i=0; i<arr.length();i++) {
                listOfStudents.add((arr.getJSONObject(i).getString("name")+","+arr.getJSONObject(i).getString("cv")));
            }

        }
        catch (com.parse.ParseException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }



        return listOfStudents;
    }

    public static void saveList(String lectureName, String course, String code, List<StudentProperties> studentList, String date){



        //if exist only need update

        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.whereEqualTo("LectureName", lectureName);
        query.whereEqualTo("Date", date);
        query.whereEqualTo("Course", course);
        query.whereEqualTo("Code", code);

        try {
            if (query.count() == 0) {

                ParseObject list = new ParseObject("List");
                list.put("LectureName", lectureName);
                list.put("Course", course);
                list.put("Code", code);
                list.put("Date", date);
                JSONArray arr = new JSONArray();

                for (StudentProperties s : studentList) {
                    JSONObject pnObj = new JSONObject();

                    pnObj.put("cv", s.getId());
                    pnObj.put("name", s.getName());
                    arr.put(pnObj);

                }
                list.put("Names", arr.toString());
                list.saveInBackground();


            }

            else{
                ParseObject courseResult = ((ParseObject) query.getFirst());

                JSONArray arr = new JSONArray();

                for (StudentProperties s : studentList) {
                    JSONObject pnObj = new JSONObject();

                    pnObj.put("cv", s.getId());
                    pnObj.put("name", s.getName());
                    arr.put(pnObj);

                }

                courseResult.put("Names",arr.toString());
                courseResult.save();
            }

        }catch (JSONException e) {
            e.printStackTrace();
     }

         catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    //user query
    public static ArrayList<String> runQuery(String from,String to ,
                                             String courseId) {

        ArrayList<ParseObject> listTrainings = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");

        query.whereEqualTo("Code", courseId);

        try {
            listTrainings = (ArrayList<ParseObject>) (query.find());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date, toDate, fromDate;
        for (ParseObject course : listTrainings) {
            try {
                date = sdf.parse(course.get("Date").toString());
                toDate = sdf.parse(to);
                fromDate = sdf.parse(from);
                if ((date.after(fromDate) && toDate.after(date))|| date.equals(fromDate)||date.equals(toDate)) {
                    list.add(course.get("Names").toString());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        return list;
    }


    //Tamar's function - set face uri per student
    public static void saveFaceUri( String faceIdToAdd, String faceUri,String cv,String personId,String courseId ,String studentName,String courseName,String code,Context context){

        Set<String> faceIds = getAllFaceIdsByStudent(personId, context);
        Set<String> newFaceIds = new HashSet<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);
        JSONArray arr;
        String studentList="";
        try {
            if (query.count()==0 )
            {
                createPerson(studentName,cv,personId,courseId,courseName,code,context );
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

    //set face in local memory
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


        ParseObject result;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("personId", personId);

        try{
            result =(ParseObject)query.getFirst();

            JSONArray arr = new JSONArray(result.getString("faces"));

            // ArrayList<String> cvList = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                String face = arr.getJSONObject(i).getString("faceId");
                if( faceIdsToDelete.contains(face))
                    arr.remove(i);

            }
            if ( arr.length()==0)
            {
                result.put("faces","{}");
            }
            else {
                result.put("faces", arr.toString());
            }


            result.saveInBackground();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        catch (com.parse.ParseException e) {

        }


    }
}
