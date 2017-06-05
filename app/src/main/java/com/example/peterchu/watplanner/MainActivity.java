package com.example.peterchu.watplanner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;

import com.example.peterchu.watplanner.Models.Course;
import com.example.peterchu.watplanner.Models.CourseResponse;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CourseResponse> call = apiService.getCourses("1175", Constants.API_KEY);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse>call, Response<CourseResponse> response) {
                Log.d("MainActivity", response.toString());
                List<Course> courses = response.body().getData();
                Log.d("MyActivity", "Number of courses received: " + courses.size());
            }

            @Override
            public void onFailure(Call<CourseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e("MyActivity", t.toString());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
