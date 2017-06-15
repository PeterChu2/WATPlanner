package com.example.peterchu.watplanner.Views.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;

import java.util.List;

/**
 * Created by peterchu on 2017-06-12.
 */

public class CourseListAdapter extends ArrayAdapter<Course> {
    public CourseListAdapter(@NonNull Context context, @LayoutRes int resource,
                             @NonNull List<Course> courses) {
        super(context, resource, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.course_list_item_view,
                    parent, false);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.listItemCourseName);
        nameTextView.setText(course.getName());
        TextView titleTextView = (TextView) convertView.findViewById(R.id.listItemCourseTitle);
        titleTextView.setText(course.getTitle());
        TextView conflictFlagTextView = (TextView) convertView.findViewById(
                R.id.listItemCourseConflict);
        // TODO: check for conflicts here and set this appropriately
        conflictFlagTextView.setVisibility(View.VISIBLE);
        // Return the completed view to render on screen
        return convertView;
    }
}
