package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.microsoft.projectoxford.face.samples.R;

import java.util.List;

/**
 * Created by tamarazu on 5/19/2016.
 */
public class CustomListAdapterForDataList extends ArrayAdapter<DataList>{

    private LayoutInflater inflater;
    private Context context;
    //constructor
    public CustomListAdapterForDataList(Context context, int resource, List<DataList> rows) {
        super(context, resource, rows);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    //method that get row in the list and initilize it
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.list_row, null);
        }

        DataList row = getItem(position);

        if (row != null) {
            TextView number = (TextView) view.findViewById(R.id.courseName);
            TextView date = (TextView) view.findViewById(R.id.courseId);


            if (number != null) {
                number.setText(row.getTrainingNum());
            }
            if (date != null) {
                date.setText(row.getDate() + "");
            }

        }

        return view;
    }
}
