package com.example.peterchu.watplanner.Views.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        // HACK for demo -- ENVS 200 and ECE 458 actually conflict
        Set<String> courseNameSet = new HashSet<String>();
        for(int i = 0; i < this.getCount(); i++) {
            Course c = getItem(i);
            courseNameSet.add(c.getName());
        }
        if (courseNameSet.contains("ECE 458") && courseNameSet.contains("ENVS 200") &&
                (course.getName().equals("ECE 458") || course.getName().equals("ENVS 200"))) {
            conflictFlagTextView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
