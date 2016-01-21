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


import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            listCourse =( ArrayList<ParseObject>)query.find();
        }
        catch (com.parse.ParseException e){

        }

    for (ParseObject course :listCourse) {

        listOfCourse.add(course.get("CourseName").toString());
    }
        return listOfCourse;
    }




    //with parse
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
             groupName.put("studentList", "");
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

    public static Set<String> getAllPersonIds(String personGroupId, Context context) {
        SharedPreferences personIdSet =
                context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        return personIdSet.getStringSet("PersonIdSet", new HashSet<String>());
    }

    public static String getPersonName(String personId, String personGroupId, Context context) {
        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);
        return personIdNameMap.getString(personId, "");
    }

    public static void setPersonName(String personIdToAdd, String personName, String personGroupId, Context context) {
        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        personIdNameMapEditor.putString(personIdToAdd, personName);
        personIdNameMapEditor.commit();

        Set<String> personIds = getAllPersonIds(personGroupId, context);
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

        Set<String> personIds = getAllPersonIds(personGroupId, context);
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
