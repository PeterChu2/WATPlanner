package com.example.peterchu.watplanner.data;

import android.app.Activity;

import com.example.peterchu.watplanner.Constants;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;

import java.util.List;
import java.util.Set;

/**
 * Created by peterchu on 2017-07-14.
 */

public interface IDataRepository {
    /**
     * Removes a course from the user's saved courses
     */
    public void removeUserCourse(int courseId);

    /**
     * Adds a course from the user's saved courses
     */
    public void addUserCourse(int courseId);

    /**
     * This performs the initial syncing of the app:
     * 1. Retrieve all courses for the current term
     * 2. Save these courses into the local database
     * If data is already synced, this method returns immediately.
     */
    public void syncData(final DataRepository.SyncDataCallback callback);


    /**
     * Attempts to retrieve a course's schedule from DB, fetches from Waterloo's API if not found, inserting
     * into DB prior to returning as a list of CourseComponents
     */
    public void findOrGetCourseSchedule(final Course course,
                                        final DataRepository.CourseScheduleCallback callback,
                                        final Activity activity);

    public List<List<CourseComponent>> getLectures(int courseId);

    public List<List<CourseComponent>> getSeminars(int courseId);

    public List<List<CourseComponent>> getLabs(int courseId);

    public List<List<CourseComponent>> getTutorials(int courseId);

    /**
     * Fetches in-depth details of a course
     *
     * @param subject      subject code (e.g. ECE)
     * @param courseNumber number associated with subject (e.g. 105)
     * @param callback     callback to trigger when response is received
     */
    public void getCourseDetails(String subject,
                                 String courseNumber,
                                 final DataRepository.CourseDetailsCallback callback);

    /**
     * Returns all Courses stored in the SQL database
     */
    public List<Course> getAllCourses();

    public List<Course> getCourses(String[] ids);

    public Course getCourse(String subject, String courseNumber);

    public Course getCourse(int courseId);

    public Set<String> getUserCourses();
}
