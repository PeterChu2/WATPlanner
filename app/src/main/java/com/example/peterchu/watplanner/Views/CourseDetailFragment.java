package com.example.peterchu.watplanner.Views;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;

/**
 * A fragment representing a single Course detail screen.
 */
public class CourseDetailFragment extends Fragment {
    public static final String ARG_COURSE_ID = "course_id";

    private Course mCourse;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_COURSE_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            final DatabaseHandler dbHandler = new DatabaseHandler(this.getContext());
            mCourse = dbHandler.getCourse(getArguments().getInt(ARG_COURSE_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(
                    R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mCourse.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_detail, container, false);

        if (mCourse == null) {
            return rootView;
        }

        ((TextView) rootView.findViewById(R.id.course_detail)).setText(mCourse.getTitle());

        return rootView;
    }
}
