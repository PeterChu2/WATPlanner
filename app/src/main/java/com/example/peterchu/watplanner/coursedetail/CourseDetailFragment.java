package com.example.peterchu.watplanner.coursedetail;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peterchu.watplanner.BaseView;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.R;

/**
 * A fragment representing a single Course detail screen.
 */
public class CourseDetailFragment extends Fragment implements BaseView<CourseDetailPresenter> {

    public static final String ARG_COURSE_ID = "course_id";

    private CourseSchedule mCourseSchedule;
    private CourseDetailPresenter presenter;
    private View rootView;

    @Override
    public void setPresenter(CourseDetailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.course_detail_fragment, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onFabClicked();
            }
        });
        return rootView;
    }

    public void setCourseDetails(CourseDetails courseDetails) {
        ((TextView) rootView.findViewById(R.id.course_title)).setText(courseDetails.getTitle());
//        ((TextView) rootView.findViewById(R.id.course_enrolment)).setText(
//                String.format("%i/%i", mCourseSchedule.getEnrollmentCapacity(),
//                        mCourseSchedule.getEnrollmentTotal()));
        ((TextView) rootView.findViewById(R.id.course_description)).setText(
                courseDetails.getDescription());
        ((TextView) rootView.findViewById(R.id.course_prerequisites)).setText(
                courseDetails.getPrerequisites());
        ((TextView) rootView.findViewById(R.id.course_antirequisites)).setText(
                courseDetails.getAntirequisites());
    }

    public void setCourseSchedule(CourseSchedule courseSchedule) {
        mCourseSchedule = courseSchedule;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String title) {
        CollapsingToolbarLayout appBarLayout =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        appBarLayout.setTitle(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }
}
