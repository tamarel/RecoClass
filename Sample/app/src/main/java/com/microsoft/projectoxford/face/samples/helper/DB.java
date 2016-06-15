package com.microsoft.projectoxford.face.samples.helper;

import android.content.Context;
import android.widget.Switch;

import com.microsoft.projectoxford.face.samples.QueriesActivity;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.QueryRow;
import com.microsoft.projectoxford.face.samples.persongroupmanagement.StudentProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tamarazu on 6/13/2016.
 */
public class DB {

    static ArrayList<QueryRow> studentList = new ArrayList<>();

    //user query
    public static String runQuery(String from, String to,
                                  String courseId, String action, String act, int number, Context context) {
        studentList.clear();
        JSONArray arr;
        ArrayList<String> attendanceLists = StorageHelper.runQuery(from, to, courseId);
        //counting sort

        ArrayList<String >  s = new ArrayList<>(StorageHelper.getAllStudentByCourse(courseId, context));

        try {

            for (String student: s) {
               String [] studentProperties = student.split(",");

                studentList.add(new QueryRow(studentProperties[0],studentProperties[1],0));

            }

            for (String list : attendanceLists) {
                arr = new JSONArray(list);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject student = arr.getJSONObject(i);
                    add(student.get("cv").toString());

                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<QueryRow> result = new ArrayList<>();
        result.clear();
        switch (action) {
            case "number of attendance":
                if (act.equals(">")) {
                    for (QueryRow student : studentList) {
                        if (student.getNumber() > number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("<")) {
                    for (QueryRow student : studentList) {
                        if (student.getNumber() < number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("=")) {

                    for (QueryRow student : studentList) {
                        if (student.getNumber() == number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("!=")) {
                    for (QueryRow student : studentList) {
                        if (student.getNumber() != number) {
                            result.add(student);                        }
                    }
                }
                break;
            case "number of nonAttendance":
                int numberOfTraining = attendanceLists.size();
                if (act.equals(">")) {
                    for (QueryRow student : studentList) {
                        if ((numberOfTraining - student.getNumber()) >= number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("<")) {
                    for (QueryRow student : studentList) {
                        if (numberOfTraining - student.getNumber() < number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("=")) {

                    for (QueryRow student : studentList) {
                        if (numberOfTraining - student.getNumber() == number) {
                            result.add(student);
                        }
                    }
                } else if (act.equals("!=")) {
                    for (QueryRow student : studentList) {
                        if (numberOfTraining - student.getNumber() != number) {
                            result.add(student);
                        }
                    }
                }

        }
        JSONArray array = new JSONArray();
        try {



            for (QueryRow student : result) {
                JSONObject object = new JSONObject();
                object.put("name", student.getName());
                object.put("id", student.getId());
                object.put("number", student.getNumber());
                array.put(object);
            }
        } catch (JSONException e) {        }

        return array.toString();
}


    public static void add(String cv){

        for(QueryRow student : studentList){
            if ( student.getId().equals(cv)){
                student.setNumber((student.getNumber()+1));
            }
        }
    }

}
