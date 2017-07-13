package com.example.peterchu.watplanner.coursedetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.data.DataRepository;

/**
 * An activity representing a single Course detail screen.
 */
public class CourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        CourseDetailFragment courseDetailFragment =
                (CourseDetailFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (courseDetailFragment == null) {
            courseDetailFragment = new CourseDetailFragment();
        }

        new CourseDetailPresenter(courseDetailFragment,
                getIntent().getIntExtra(CourseDetailFragment.ARG_COURSE_ID, -1),
                DataRepository.getDataRepository(this),
                this);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, courseDetailFragment)
                .commit();
    }
}
