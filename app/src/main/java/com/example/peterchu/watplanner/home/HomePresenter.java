package com.example.peterchu.watplanner.home;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Database.DBHandlerCallback;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleResponse;
import com.example.peterchu.watplanner.Networking.ApiInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class HomePresenter implements BasePresenter {

    private HomeFragment homeFragment;
    private ApiInterface apiInterface;
    private DatabaseHandler dbHandler;
    private SharedPreferences sharedPreferences;

    private boolean isFirstLoad = true;

    public HomePresenter(HomeFragment homeFragment,
                         ApiInterface apiInterface,
                         DatabaseHandler dbHandler,
                         SharedPreferences sharedPreferences) {
        this.homeFragment = homeFragment;
        this.apiInterface = apiInterface;
        this.dbHandler = dbHandler;
        this.sharedPreferences = sharedPreferences;
        homeFragment.setPresenter(this);
    }

    @Override
    public void start() {
        // TODO: remove in PROD :)
        dbHandler.destroyAndRecreateDb();

        if (isFirstLoad) {
            final Set<String> savedCourses = sharedPreferences.getStringSet(
                    Constants.SHARED_PREFS_ADDED_COURSES,
                    new HashSet<String>());

            if (dbHandler.getCoursesCount() == 0) {
                Call<CourseResponse> call = apiInterface.getCourses("1175", Constants.API_KEY);

                call.enqueue(new Callback<CourseResponse>() {
                    @Override
                    public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                        Log.d("HomePresenter", response.toString());
                        List<Course> courses = response.body().getData();
                        Log.d("HomePresenter", "Number of courses received: " + courses.size());

                        List<Course> userCourses = new ArrayList<>();
                        for (Course c : courses) {
                            if (savedCourses.contains(String.valueOf(c.getId()))) {
                                userCourses.add(c);
                            }
                        }

                        homeFragment.addCourses(userCourses);
                        try {
                            Log.d("HomePresenter", "Trying to add courses");
                            dbHandler.addCourses(courses, new DBHandlerCallback() {
                                @Override
                                public void onFinishTransaction(DatabaseHandler dbHandler) {
                                    Log.d("DBHandlerCallback", "Finished adding courses count: " + dbHandler.getCoursesCount());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("DatabaseHandler", "Failed to add courses!!");
                        }
                    }

                    @Override
                    public void onFailure(Call<CourseResponse> call, Throwable t) {
                        // Log error here since request failed
                        Log.e("HomePresenter", t.toString());
                    }
                });
            } else if (!savedCourses.isEmpty()){
                List<Course> courses = dbHandler.getCourses(
                        savedCourses.toArray(new String[savedCourses.size()]));
                homeFragment.addCourses(courses);
            }


            if(dbHandler.getSchedulesCount() == 0) {
                Call<CourseScheduleResponse> scheduleCall = apiInterface.getSubjectCourseSchedules(
                        "1175", "ECE", Constants.API_KEY
                );
                scheduleCall.enqueue(new Callback<CourseScheduleResponse>() {
                    @Override
                    public void onResponse(Call<CourseScheduleResponse> call,
                                           Response<CourseScheduleResponse> response) {
                        Log.d("HomePresenter", response.toString());
                        List<CourseSchedule> schedules = response.body().getData();
                        Log.d("HomePresenter", "Number of courses schedules received: " + schedules.size());
                        try {
                            Log.d("HomePresenter", "Trying to add course schedules");

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
                        Log.e("HomePresenter", t.toString());
                    }
                });
            } else {
                Log.d("MyActivity", "Num schedules in db: " + dbHandler.getSchedulesCount());
                List<CourseComponent> schedules = dbHandler.getCourseSchedule("ECE", "103");
                for (CourseComponent c : schedules) {
                    Log.d("MyActivity", c.toString());
                }
            }

            isFirstLoad = false;
        }
    }

    public void onCourseAdd(String subject, String catalogNumber) {

        Course course = dbHandler.getCourseByCourseCode(subject, catalogNumber);
        if (course == null) {
            Toast.makeText(homeFragment.getContext(),
                    "Course is invalid or not offered this term!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        homeFragment.addCourses(Arrays.asList(course));
        Set<String> addedCourses = sharedPreferences.getStringSet(
                Constants.SHARED_PREFS_ADDED_COURSES, new HashSet<String>());
        addedCourses.add(String.valueOf(course.getId()));

        sharedPreferences.edit()
                .remove(Constants.SHARED_PREFS_ADDED_COURSES)
                .putStringSet(Constants.SHARED_PREFS_ADDED_COURSES, addedCourses)
                .apply();
    }
}
