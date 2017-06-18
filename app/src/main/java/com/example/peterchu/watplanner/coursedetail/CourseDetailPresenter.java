package com.example.peterchu.watplanner.coursedetail;

import android.util.Log;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Course.CourseDetailsResponse;
import com.example.peterchu.watplanner.Networking.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CourseDetailPresenter implements BasePresenter {

    private CourseDetailFragment courseDetailFragment;
    private ApiInterface apiInterface;
    private DatabaseHandler dbHandler;

    private int courseId;

    private boolean isFirstLoad = true;

    public CourseDetailPresenter(CourseDetailFragment courseDetailFragment,
                                 int courseId,
                                 ApiInterface apiInterface,
                                 DatabaseHandler dbHandler) {
        this.courseDetailFragment = courseDetailFragment;
        this.courseId = courseId;
        this.apiInterface = apiInterface;
        this.dbHandler = dbHandler;
        courseDetailFragment.setPresenter(this);
    }

    @Override
    public void start() {
        if (isFirstLoad) {
            Course course = dbHandler.getCourse(courseId);
            courseDetailFragment.setTitle(course.getName());\
//            final CourseSchedule courseSchedule = dbHandler.getCourseSchedule(course.getSubject(),
//                    course.getNumber());
            Call<CourseDetailsResponse> call = apiInterface.getCourseDetails(
                    course.getSubject(),
                    course.getNumber(),
                    Constants.API_KEY);
            call.enqueue(new Callback<CourseDetailsResponse>() {
                @Override
                public void onResponse(Call<CourseDetailsResponse> call,
                                       Response<CourseDetailsResponse> response) {
                    Log.d("CourseDetailPresenter", response.toString());
                    CourseDetails courseDetails = response.body().getData();
                    courseDetailFragment.setCourseDetails(courseDetails);
                }

                @Override
                public void onFailure(Call<CourseDetailsResponse> call, Throwable t) {
                    Log.e("CourseDetailPresenter", t.toString());
                }
            });
            isFirstLoad = false;
        }
    }

    public void onFabClicked() {
        // TODO: Add course to user's courses
    }
}
