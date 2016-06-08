//Ex3 - Omri sharvit - 301590204
//Ex3 - Tamareliyahou - 203380332

package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import com.microsoft.projectoxford.face.samples.R;

import java.util.List;

//This class build the listview row
public class CustomListAdapter extends ArrayAdapter<CourseProperties> {

    private LayoutInflater inflater;
    private Context context;
    //constructor
    public CustomListAdapter(Context context, int resource, List<CourseProperties> rows) {
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

        CourseProperties row = getItem(position);

        if (row != null) {
            TextView name = (TextView) view.findViewById(R.id.courseName);
            TextView id = (TextView) view.findViewById(R.id.courseId);


            if (name != null) {
                name.setText(row.getCourseName());
            }
            if (id != null) {
                id.setText(row.getCourseId() + "");
            }

        }

        return view;
    }

}
