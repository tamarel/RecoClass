package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.IdentificationActivity;
import com.microsoft.projectoxford.face.samples.R;

import java.util.List;

/**
 * Created by tamarazu on 5/23/2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.R;

import java.util.List;


public class CustomListStudent  extends ArrayAdapter<StudentProperties> {

    private LayoutInflater inflater;
    private Context context;

    //constructor
    public CustomListStudent(Context context, int resource, List<StudentProperties> rows) {
        super(context, resource, rows);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    //method that get row in the list and initilize it
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.student_row, null);
        }

        StudentProperties row = getItem(position);

        if (row != null) {
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView id = (TextView) view.findViewById(R.id.id);

            if (name != null) {
                name.setText(row.getName());
            }
            if (id != null) {
                id.setText(row.getId() + "");
            }

        }

        return view;
    }

}
