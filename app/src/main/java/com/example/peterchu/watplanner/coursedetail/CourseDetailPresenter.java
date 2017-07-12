package com.example.peterchu.watplanner.coursedetail;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Course.CourseDetails;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleComponent;
import com.example.peterchu.watplanner.data.DataRepository;

import java.util.List;
import java.util.Set;

class CourseDetailPresenter implements BasePresenter {

    private CourseDetailFragment courseDetailFragment;
    private DataRepository dataRepository;
    private int courseId;

    private boolean isFirstLoad = true;
    private boolean isAddedCourse;

    CourseDetailPresenter(CourseDetailFragment courseDetailFragment,
                          int courseId,
                          DataRepository dataRepository) {
        this.courseDetailFragment = courseDetailFragment;
        this.courseId = courseId;
        this.dataRepository = dataRepository;
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

            dataRepository.getCourseSchedule(
                    course,
                    new DataRepository.CourseScheduleCallback() {
                        @Override
                        public void onCourseScheduleRetrieved(
                                List<CourseScheduleComponent> schedules) {
                            courseDetailFragment.setCourseSchedule(schedules);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });


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
    }
}
