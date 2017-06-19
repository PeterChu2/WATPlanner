package com.example.peterchu.watplanner.Views.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseListAdapter extends ArrayAdapter<Course> {
    private Context context;
    private CourseRemoveClickListener listener;

    public CourseListAdapter(@NonNull Context context, @LayoutRes int resource,
                             @NonNull List<Course> courses, CourseRemoveClickListener listener) {
        super(context, resource, courses);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Course course = getItem(position);
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
        final ImageView options = (ImageView) convertView.findViewById(R.id.options_button);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(options, course);
            }
        });

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

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, final Course course) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_card, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                listener.onCourseRemoveClicked(course);
                return true;
            }
        });
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    public interface CourseRemoveClickListener {
        void onCourseRemoveClicked (Course course);
    }
}
