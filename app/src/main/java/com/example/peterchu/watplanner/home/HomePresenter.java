package com.example.peterchu.watplanner.home;

import android.widget.Toast;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.data.DataRepository;

import java.util.List;
import java.util.Set;

class HomePresenter implements BasePresenter {

    private HomeFragment homeFragment;
    private DataRepository dataRepository;

    public HomePresenter(HomeFragment homeFragment,
                         DataRepository dataRepository) {
        this.homeFragment = homeFragment;
        this.dataRepository = dataRepository;
        homeFragment.setPresenter(this);
    }

    @Override
    public void start() {
        // If data is already synced, the callback executes immediately.
        dataRepository.syncData(new DataRepository.SyncDataCallback() {
            @Override
            public void onDataSynced() {
                // Load user's saved courses into the list
                final Set<String> savedCourses = dataRepository.getUserCourses();
                if (!savedCourses.isEmpty()) {
                    List<Course> courses = dataRepository.getCourses(
                            savedCourses.toArray(new String[savedCourses.size()]));
                    homeFragment.emptyCourseList();
                    homeFragment.addCourses(courses);
                }
            }

            @Override
            public void onDataSyncFailure() {
                // TODO: Implement retry. Continual failure should alert user.
            }
        });
    }

    public void onCourseAdd(String subject, String catalogNumber) {
        Course course = dataRepository.getCourse(subject, catalogNumber);
        if (course == null) {
            Toast.makeText(homeFragment.getContext(),
                    "Course is invalid or not offered this term!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        dataRepository.addUserCourse(course.getId());
    }

    public void onCourseRemoved(Course course) {
        dataRepository.removeUserCourse(course.getId());
        homeFragment.removeCourse(course);
    }

    public void onSearchOpened() {
        homeFragment.addSearchSuggestions(dataRepository.getAllCourses());
    }

    public boolean onSubmitSearchQuery(String subject, String catalogNumber) {
        Course c = dataRepository.getCourse(subject, catalogNumber);
        return homeFragment.openDetailView(c == null ? null : c.getId());
    }
}
