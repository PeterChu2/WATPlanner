package com.example.peterchu.watplanner;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.peterchu.watplanner.Database.DBHandlerCallback;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleResponse;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;
import com.example.peterchu.watplanner.Views.Adapters.CourseListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final Pattern courseCodePattern = Pattern.compile("(\\w+)[\\s]*([1-9]\\w+)");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        final DatabaseHandler dbHandler = new DatabaseHandler(this);
        final ListView userCoursesList = (ListView) findViewById(R.id.userCoursesList);

        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(MainActivity.this);
        final List<Course> currentCourses = new ArrayList<Course>();
        final Set<String> savedCourses = sharedPreferences.getStringSet(Constants.SHARED_PREFS_ADDED_COURSES,
                new HashSet<String>());
        userCoursesList.setAdapter(new CourseListAdapter(MainActivity.this,
                R.layout.course_list_item_view, currentCourses));

        // dbHandler.onUpgrade(dbHandler.getWritableDatabase(), 1,2);
        // Migration hack for now
       // dbHandler.destroyAndRecreateDb();

        try {
            dbHandler.getCoursesCount();
            dbHandler.getSchedulesCount();
        } catch (Exception e) {
            // recreate DB
            dbHandler.destroyAndRecreateDb();
        }

        if(dbHandler.getCoursesCount() == 0) {
            Call<CourseResponse> call = apiService.getCourses("1175", Constants.API_KEY);

            call.enqueue(new Callback<CourseResponse>() {
                @Override
                public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                    Log.d("MainActivity", response.toString());
                    List<Course> courses = response.body().getData();
                    if (savedCourses != null) {
                        for (Course c : courses) {
                            if (savedCourses.contains(String.valueOf(c.getId()))) {
                                currentCourses.add(c);
                            }
                        }
                        ((CourseListAdapter) (userCoursesList.getAdapter())).notifyDataSetChanged();
                    }

                    Log.d("MyActivity", "Number of courses received: " + courses.size());

                    try {
                        Log.d("MyActivity", "Trying to add courses");

                        final DBHandlerCallback callback = new DBHandlerCallback() {
                            @Override
                            public void onFinishTransaction(DatabaseHandler dbHandler) {
                                Log.d("DBHandlerCallback", "Finished adding courses count: " + dbHandler.getCoursesCount());
                            }
                        };

                        dbHandler.addCourses(courses, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DatabaseHandler", "Failed to add courses!!");
                    }
                }

                @Override
                public void onFailure(Call<CourseResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("MyActivity", t.toString());
                }
            });
        } else {
            Log.d("MyActivity", "Num courses in db: " + dbHandler.getCoursesCount());
            List<Course> courses = dbHandler.getCourses(savedCourses.toArray(
                    new String[savedCourses.size()]));
            for (Course c : courses) {
                currentCourses.add(c);
            }
            ((CourseListAdapter) (userCoursesList.getAdapter())).notifyDataSetChanged();
        }

        if(dbHandler.getSchedulesCount() == 0) {
            Call<CourseScheduleResponse> scheduleCall = apiService.getSubjectCourseSchedules(
                    "1175", "ECE", Constants.API_KEY
            );
            scheduleCall.enqueue(new Callback<CourseScheduleResponse>() {
                @Override
                public void onResponse(Call<CourseScheduleResponse> call,
                                       Response<CourseScheduleResponse> response) {
                    Log.d("MainActivity", response.toString());
                    List<CourseSchedule> schedules = response.body().getData();
                    Log.d("MainActivity", "Number of courses schedules received: " + schedules.size());
                    try {
                        Log.d("MyActivity", "Trying to add course schedules");

                        final DBHandlerCallback callback = new DBHandlerCallback() {
                            @Override
                            public void onFinishTransaction(DatabaseHandler dbHandler) {
                                Log.d("DBHandlerCallback", "Finished adding schedules count: " + dbHandler.getSchedulesCount());
                            }
                        };

                        dbHandler.addSchedules(schedules, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DatabaseHandler", "Failed to add schedules!!");
                    }
                }

                @Override
                public void onFailure(Call<CourseScheduleResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("MainActivity", t.toString());
                }
            });
        } else {
            Log.d("MyActivity", "Num schedules in db: " + dbHandler.getSchedulesCount());
            List<CourseComponent> schedules = dbHandler.getAllCourseSchedules();
            for (CourseComponent c : schedules) {
                Log.d("MyActivity", c.toString());
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add a course to your schedule");

                final EditText inputEditText = new EditText(MainActivity.this);
                inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputEditText);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = inputEditText.getText().toString();
                        Matcher matcher = courseCodePattern.matcher(input);
                        if (!matcher.find()) {
                            Toast.makeText(MainActivity.this, "Invalid course code!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String subject = matcher.group(1);
                        String catalogNumber = matcher.group(2);
                        Course course = dbHandler.getCourseByCourseCode(subject, catalogNumber);
                        if (course == null) {
                            Toast.makeText(MainActivity.this, "Course is invalid or not offered " +
                                            "this term!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        currentCourses.add(course);
                        ((CourseListAdapter) (userCoursesList.getAdapter())).notifyDataSetChanged();
                        SharedPreferences sharedPreferences = PreferenceManager.
                                getDefaultSharedPreferences(MainActivity.this);
                        Set<String> savedPrefs = sharedPreferences.getStringSet(
                                Constants.SHARED_PREFS_ADDED_COURSES, new HashSet<String>());
                        savedPrefs.add(String.valueOf(course.getId()));

                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.remove(Constants.SHARED_PREFS_ADDED_COURSES);
                        e.apply();
                        e.putStringSet(Constants.SHARED_PREFS_ADDED_COURSES, savedPrefs);
                        e.apply();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
