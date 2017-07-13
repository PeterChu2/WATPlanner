package com.example.peterchu.watplanner.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Database.DBHandlerCallback;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Course.CourseDetailsResponse;
import com.example.peterchu.watplanner.Models.Course.CourseResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleResponse;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {

    private static DataRepository INSTANCE = null;

    private final ApiInterface apiInterface;
    private final SharedPreferences sharedPreferences;
    private final DatabaseHandler databaseHandler;

    private DataRepository(Context context) {
        this.apiInterface = ApiClient.getClient().create(ApiInterface.class);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.databaseHandler = DatabaseHandler.getInstance(context);
    }

    public static DataRepository getDataRepository(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(context);
        }
        return INSTANCE;
    }

    /**
     * Removes a course from the user's saved courses
     */
    public void removeUserCourse(int courseId) {
        Set<String> addedCourses = getUserCourses();
        addedCourses.remove(String.valueOf(courseId));
        sharedPreferences.edit()
                .remove(Constants.SHARED_PREFS_ADDED_COURSES)
                .apply();
        sharedPreferences.edit()
                .putStringSet(Constants.SHARED_PREFS_ADDED_COURSES, addedCourses)
                .apply();
    }

    /**
     * Adds a course from the user's saved courses
     */
    public void addUserCourse(int courseId) {
        Set<String> addedCourses = getUserCourses();
        addedCourses.add(String.valueOf(courseId));
        sharedPreferences.edit()
                .remove(Constants.SHARED_PREFS_ADDED_COURSES)
                .apply();
        sharedPreferences.edit()
                .putStringSet(Constants.SHARED_PREFS_ADDED_COURSES, addedCourses)
                .apply();
    }

    /**
     * This performs the initial syncing of the app:
     * 1. Retrieve all courses for the current term
     * 2. Save these courses into the local database
     * If data is already synced, this method returns immediately.
     */
    public void syncData(final SyncDataCallback callback) {
        // TODO: Should grab current term from API and use that value instead of hardcoding it
        if (databaseHandler.getCoursesCount() == 0) {
            Log.d("DataRepository", "Performing data sync");
            apiInterface.getCourses("1175", Constants.API_KEY)
                    .enqueue(new Callback<CourseResponse>() {
                        @Override
                        public void onResponse(Call<CourseResponse> call,
                                               Response<CourseResponse> response) {
                            List<Course> courses = response.body().getData();
                            Log.d("DataRepository", "API - # of courses returned: "
                                    + courses.size());

                            databaseHandler.addCourses(courses, new DBHandlerCallback() {
                                @Override
                                public void onFinishTransaction(DatabaseHandler dbHandler) {
                                    Log.d("DataRepository", "Database synced with courses");
                                    callback.onDataSynced();
                                }

                                @Override
                                public void onTransactionFailed(Exception e) {
                                    Log.d("DataRepository", "Database failed to sync!");
                                    e.printStackTrace();
                                    callback.onDataSyncFailure();
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<CourseResponse> call, Throwable t) {
                            Log.d("DataRepository", "Failed to retrieve courses from API!");
                            t.printStackTrace();
                            callback.onDataSyncFailure();
                        }
                    });
        } else {
            Log.d("DataRepository", "Data is already synced");
            callback.onDataSynced();
        }
    }

    /**
     * Attempts to retrieve a course's schedule from DB, fetches from Waterloo's API if not found, inserting
     * into DB prior to returning as a list of CourseComponents
     */
    public void findOrGetCourseSchedule(final Course course, final CourseScheduleCallback callback, final Activity activity) {
        List<CourseComponent> components = databaseHandler.getCourseSchedule(course.getSubject(), course.getNumber());
        Log.d("FIND OR GET", ""+components.size());
        if (components.size() > 0) {
            callback.onCourseScheduleRetrieved(components);
            return;
        }
        Log.d("DataRepository", String.format("No schedule for %s found, fetching from API", course.toString()));
        apiInterface.getCourseSchedule(
                course.getSubject(),
                course.getNumber(),
                Constants.API_KEY)
                .enqueue(new Callback<CourseScheduleResponse>() {
                    @Override
                    public void onResponse(Call<CourseScheduleResponse> call,
                                           Response<CourseScheduleResponse> response) {
                        DBHandlerCallback addSchedulesCallback = new DBHandlerCallback() {
                            @Override
                            public void onFinishTransaction(final DatabaseHandler dbHandler) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<CourseComponent> components = dbHandler.getCourseSchedule(course.getSubject(), course.getNumber());
                                        callback.onCourseScheduleRetrieved(components);
                                        Log.d("FIND OR GET", ""+components.size());
                                    }
                                });
                            }

                            @Override
                            public void onTransactionFailed(Exception e) {
                                Log.e("DataRepository", "Unable to add schedules");
                            }
                        };
                        databaseHandler.addSchedules(course, response.body().getData(), addSchedulesCallback);

                    }

                    @Override
                    public void onFailure(Call<CourseScheduleResponse> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    public List<CourseComponent> getLectures(int courseId) {
        return databaseHandler.getCourseComponents(courseId, Constants.LEC);
    }

    public List<CourseComponent> getSeminars(int courseId) {
        return databaseHandler.getCourseComponents(courseId, Constants.SEM);
    }

    public List<CourseComponent> getLabs(int courseId) {
        return databaseHandler.getCourseComponents(courseId, Constants.LAB);
    }

    public List<CourseComponent> getTutorials(int courseId) {
        return databaseHandler.getCourseComponents(courseId, Constants.TUT);
    }



    /**
     * Fetches in-depth details of a course
     *
     * @param subject      subject code (e.g. ECE)
     * @param courseNumber number associated with subject (e.g. 105)
     * @param callback     callback to trigger when response is received
     */
    public void getCourseDetails(String subject,
                                 String courseNumber,
                                 final CourseDetailsCallback callback) {
        apiInterface.getCourseDetails(
                subject,
                courseNumber,
                Constants.API_KEY)
                .enqueue(new Callback<CourseDetailsResponse>() {
                    @Override
                    public void onResponse(Call<CourseDetailsResponse> call,
                                           Response<CourseDetailsResponse> response) {
                        callback.onCourseDetailsRetrieved(response.body().getData());
                    }

                    @Override
                    public void onFailure(Call<CourseDetailsResponse> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    /**
     * Returns all Courses stored in the SQL database
     */
    public List<Course> getAllCourses() {
        return databaseHandler.getAllCourses();
    }

    /**
     * Returns Courses stored in the SQL database given an array of IDs
     */
    public List<Course> getCourses(String[] ids) {
        return databaseHandler.getCourses(ids);
    }

    public Course getCourse(String subject, String courseNumber) {
        return databaseHandler.getCourseByCourseCode(subject, courseNumber);
    }

    public Course getCourse(int courseId) {
        return databaseHandler.getCourse(courseId);
    }

    public Set<String> getUserCourses() {
        return sharedPreferences.getStringSet(
                Constants.SHARED_PREFS_ADDED_COURSES, new HashSet<String>());
    }

    public interface SyncDataCallback {
        void onDataSynced();

        void onDataSyncFailure();
    }

    public interface CourseScheduleCallback {
        void onCourseScheduleRetrieved(List<CourseComponent> schedules);

        void onFailure();
    }

    public interface CourseDetailsCallback {
        void onCourseDetailsRetrieved(CourseDetails courseDetails);

        void onFailure();
    }
}
