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

//custom list query

public class CustomListQuery  extends ArrayAdapter<QueryRow> {

    private LayoutInflater inflater;
    private Context context;

    //constructor
    public CustomListQuery(Context context, int resource, List<QueryRow> rows) {
        super(context, resource, rows);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    //method that get row in the list and initilize it
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.row_of_query, null);
        }

        QueryRow row = getItem(position);

        if (row != null) {
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView id = (TextView) view.findViewById(R.id.id);
            TextView number = (TextView) view.findViewById(R.id.number);

            if (name != null) {
                name.setText(row.getName());
            }
            if (id != null) {
                id.setText(row.getId() + "");
            }
            if (number != null) {
                number.setText(row.getNumber() + "");
            }

        }

        return view;
    }

}
