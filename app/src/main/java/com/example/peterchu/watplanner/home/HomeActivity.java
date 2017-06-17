package com.example.peterchu.watplanner.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Database.DBHandlerCallback;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleResponse;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.Views.Adapters.CourseListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        HomeFragment homeFragment =
                (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, homeFragment)
                .commit();

        homePresenter = new HomePresenter(
                homeFragment,
                ApiClient.getClient().create(ApiInterface.class),
                DatabaseHandler.getInstance(this),
                PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
