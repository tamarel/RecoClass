package com.microsoft.projectoxford.face.samples.persongroupmanagement;

/**
 * Created by tamarazu on 6/4/2016.
 */


        import android.content.Context;
        import android.graphics.Color;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.GridView;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.microsoft.projectoxford.face.samples.R;

public class buttonAdapter extends BaseAdapter {
    private Context mContext;

    // Constructor
    public buttonAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageButton imageView;
        TextView textView;

        if (convertView == null) {
            textView = new TextView(mContext);
            imageView = new ImageButton(mContext);
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else
        {
            imageView = (ImageButton) convertView;
            textView = (TextView) convertView;
        }
        textView.setText(names[position]);
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.add_list_button, R.drawable.add_course_button,
            R.drawable.settings_button, R.drawable.calendar_button,
            R.drawable.add_student_button
    };

    public String[] names = {
            "Courses", "Add course",
            "Settings", "Calendar",
            "Add student" };

}