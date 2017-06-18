package com.example.peterchu.watplanner.coursedetail;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;
import com.example.peterchu.watplanner.R;

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
                ApiClient.getClient().create(ApiInterface.class),
                DatabaseHandler.getInstance(this),
                PreferenceManager.getDefaultSharedPreferences(this));

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, courseDetailFragment)
                .commit();
    }
}
