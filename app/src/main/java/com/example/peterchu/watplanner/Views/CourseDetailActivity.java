package com.example.peterchu.watplanner.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Course.CourseDetailsResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;
import com.example.peterchu.watplanner.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a single Course detail screen.
 */
public class CourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState == null) {
            final DatabaseHandler dbHandler = new DatabaseHandler(this);
            Course course = dbHandler.getCourse(getIntent().getIntExtra(
                    CourseDetailFragment.ARG_COURSE_ID, -1));
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) this.findViewById(
                    R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(course.getName());
            }
//            final CourseSchedule courseSchedule = dbHandler.getCourseSchedule(course.getSubject(),
//                    course.getNumber());
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<CourseDetailsResponse> call = apiService.getCourseDetails(course.getSubject(),
                    course.getNumber(), Constants.API_KEY);

            call.enqueue(new Callback<CourseDetailsResponse>() {
                @Override
                public void onResponse(Call<CourseDetailsResponse> call, Response<CourseDetailsResponse> response) {
                    Log.d("MainActivity", response.toString());
                    CourseDetails courseDetails = response.body().getData();
                    // Create the detail fragment and add it to the activity
                    // using a fragment transaction.
                    Bundle arguments = new Bundle();
                    arguments.putInt(CourseDetailFragment.ARG_COURSE_ID,
                            getIntent().getIntExtra(CourseDetailFragment.ARG_COURSE_ID, -1));
                    CourseDetailFragment fragment = new CourseDetailFragment();
                    fragment.setCourseDetails(courseDetails);
//                    fragment.setCourseSchedule(courseSchedule);
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.course_detail_container, fragment)
                    .commit();

                }

                @Override
                public void onFailure(Call<CourseDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("CourseDetailFragment", t.toString());
                }
            });

            // Show the Up button in the action bar.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
