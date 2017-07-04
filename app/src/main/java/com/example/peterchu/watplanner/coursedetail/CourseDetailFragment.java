package com.example.peterchu.watplanner.coursedetail;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.BaseView;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Course detail screen.
 */
public class CourseDetailFragment extends Fragment implements BaseView<CourseDetailPresenter> {

    public static final String ARG_COURSE_ID = "course_id";

    private List<CourseComponent> mCourseSchedule;
    private CourseDetailPresenter presenter;

    private View rootView;
    private FloatingActionButton fab;

    private boolean isFabRotated = false;

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

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
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

        for (CourseComponent c : mCourseSchedule) {
            ComponentItemView componentItemView = new ComponentItemView(
                    this.getContext(),
                    c.getType(),
                    c.getSection(),
                    c.getLocation(),
                    c.getDay(),
                    c.getStartTime(),
                    c.getEndTime(),
                    c.getInstructors(),
                    c.getEnrollmentCapacity(),
                    c.getEnrollmentTotal()
            );
        }

        // Get a reference for the week view in the layout.
        WeekView weekView = (WeekView) rootView.findViewById(R.id.weekView);

        // Set an action when any event is clicked.
//        weekView.setOnEventClickListener(mEventClickListener);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                for (CourseComponent c : mCourseSchedule) {
                    events.addAll(c.toWeekViewEvents(newMonth));
                }
                return events;
            }
        });
        weekView.setMinimumHeight(5000);

        ((TextView) rootView.findViewById(R.id.course_description)).setText(
                courseDetails.getDescription());
        ((TextView) rootView.findViewById(R.id.course_prerequisites)).setText(
                courseDetails.getPrerequisites());
        ((TextView) rootView.findViewById(R.id.course_antirequisites)).setText(
                courseDetails.getAntirequisites());
    }

    public void setCourseSchedule(List<CourseComponent> courseSchedule) {
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

    public void showAddButton() {
        fab.setImageResource(R.drawable.ic_add_white_24px);
    }

    public void showRemoveButton() {
        fab.setImageResource(R.drawable.ic_clear_white_24px);
    }

    public void toggleFabRotation() {
        ViewPropertyAnimatorCompat animator = ViewCompat.animate(fab);
        if (isFabRotated) {
            animator = animator.rotation(0.0F);
            isFabRotated = false;
        } else {
            animator = animator.rotation(45.0F);
            isFabRotated = true;
        }
        animator.withLayer()
                .setDuration(300L)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void showRemovedMessage() {
        Snackbar.make(rootView, "Course removed", Snackbar.LENGTH_SHORT).show();
    }

    public void showAddedMessage() {
        Snackbar.make(rootView, "Course added", Snackbar.LENGTH_SHORT).show();
    }
}
