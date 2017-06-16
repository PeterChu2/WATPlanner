package com.example.peterchu.watplanner.Views;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.R;

/**
 * A fragment representing a single Course detail screen.
 */
public class CourseDetailFragment extends Fragment {
    public static final String ARG_COURSE_ID = "course_id";

    private CourseDetails mCourseDetails;

    private CourseSchedule mCourseSchedule;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseDetailFragment() {
    }

    public void setCourseDetails(CourseDetails courseDetails) {
        mCourseDetails = courseDetails;
    }

    public void setCourseSchedule(CourseSchedule courseSchedule) {
        mCourseSchedule = courseSchedule;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_detail, container, false);

        if (mCourseDetails == null) {
            return rootView;
        }

        ((TextView) rootView.findViewById(R.id.course_title)).setText(mCourseDetails.getTitle());
//        ((TextView) rootView.findViewById(R.id.course_enrolment)).setText(
//                String.format("%i/%i", mCourseSchedule.getEnrollmentCapacity(),
//                        mCourseSchedule.getEnrollmentTotal()));
        ((TextView) rootView.findViewById(R.id.course_description)).setText(
                mCourseDetails.getDescription());
        ((TextView) rootView.findViewById(R.id.course_prerequisites)).setText(
                mCourseDetails.getPrerequisites());
        ((TextView) rootView.findViewById(R.id.course_antirequisites)).setText(
                mCourseDetails.getAntirequisites());

        return rootView;
    }
}
