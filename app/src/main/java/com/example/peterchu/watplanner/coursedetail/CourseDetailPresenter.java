package com.example.peterchu.watplanner.coursedetail;

import android.app.Activity;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.data.DataRepository;
import com.example.peterchu.watplanner.data.IDataRepository;
import com.example.peterchu.watplanner.scheduler.CourseScheduler;
import com.example.peterchu.watplanner.scheduler.ScheduleUtils;

import java.util.List;
import java.util.Set;

class CourseDetailPresenter implements BasePresenter {

    private CourseDetailFragment courseDetailFragment;
    private IDataRepository dataRepository;
    private int courseId;

    private boolean isFirstLoad = true;
    private boolean isAddedCourse;
    private Activity activity;
    private CourseScheduler scheduler;
    private List<List<CourseComponent>> schedule;

    CourseDetailPresenter(CourseDetailFragment courseDetailFragment,
                          int courseId,
                          IDataRepository dataRepository,
                          Activity activity) {
        this.courseDetailFragment = courseDetailFragment;
        this.courseId = courseId;
        this.dataRepository = dataRepository;
        this.activity = activity;
        this.scheduler = new CourseScheduler(dataRepository);
        courseDetailFragment.setPresenter(this);
    }

    @Override
    public void start() {
        if (isFirstLoad) {
            Set<String> addedCourses = dataRepository.getUserCourses();
            if (addedCourses.contains(Integer.toString(courseId))) {
                isAddedCourse = true;
                courseDetailFragment.showRemoveButton();
            } else {
                isAddedCourse = false;
                courseDetailFragment.showAddButton();
            }

            Course course = dataRepository.getCourse(courseId);
            if (course == null) {
                throw new IllegalStateException("Course ID passed into CourseDetailView invalid!");
            }

            courseDetailFragment.setTitle(course.getName());

            dataRepository.findOrGetCourseSchedule(
                    course,
                    new DataRepository.CourseScheduleCallback() {
                        @Override
                        public void onCourseScheduleRetrieved(List<List<CourseComponent>> schedules) {
                            courseDetailFragment.setCourseSchedule(schedules);
                        }

                        @Override
                        public void onFailure() {

                        }
                    },
                    this.activity
            );

            dataRepository.getCourseDetails(
                    course.getSubject(),
                    course.getNumber(),
                    new DataRepository.CourseDetailsCallback() {
                        @Override
                        public void onCourseDetailsRetrieved(CourseDetails courseDetails) {
                            courseDetailFragment.setCourseDetails(courseDetails);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
            isFirstLoad = false;
        }
        // retrieve from cache.
        schedule = dataRepository.getCourseSchedules();
    }

    public void pause() {
        // save to cache.
        dataRepository.setCourseSchedules(schedule);
    }

    void onFabClicked() {
        if (isAddedCourse) {
            isAddedCourse = false;
            courseDetailFragment.showRemovedMessage();
            dataRepository.removeUserCourse(courseId);
        } else {
            isAddedCourse = true;
            courseDetailFragment.showAddedMessage();
            dataRepository.addUserCourse(courseId);
        }
        courseDetailFragment.toggleFabRotation();
        schedule = ScheduleUtils.getGeneratedSchedules(scheduler);
    }
}
