package com.example.peterchu.watplanner.data;

import android.app.Activity;

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
    void removeUserCourse(int courseId);

    /**
     * Adds a course from the user's saved courses
     */
    void addUserCourse(int courseId);

    /**
     * This performs the initial syncing of the app:
     * 1. Retrieve all courses for the current term
     * 2. Save these courses into the local database
     * If data is already synced, this method returns immediately.
     */
    void syncData(final DataRepository.SyncDataCallback callback, final Activity activity);


    /**
     * Attempts to retrieve a course's schedule from DB, fetches from Waterloo's API if not found, inserting
     * into DB prior to returning as a list of CourseComponents
     */
    void findOrGetCourseSchedule(final Course course,
                                        final DataRepository.CourseScheduleCallback callback,
                                        final Activity activity);

    List<List<CourseComponent>> getLectures(int courseId);

    List<List<CourseComponent>> getSeminars(int courseId);

    List<List<CourseComponent>> getLabs(int courseId);

    List<List<CourseComponent>> getTutorials(int courseId);

    /**
     * Fetches in-depth details of a course
     *
     * @param subject      subject code (e.g. ECE)
     * @param courseNumber number associated with subject (e.g. 105)
     * @param callback     callback to trigger when response is received
     */
    void getCourseDetails(String subject,
                                 String courseNumber,
                                 final DataRepository.CourseDetailsCallback callback);

    /**
     * Returns all Courses stored in the SQL database
     */
    List<Course> getAllCourses();

    List<Course> getCourses(String[] ids);

    Course getCourse(String subject, String courseNumber);

    Course getCourse(int courseId);

    Set<String> getUserCourses();

    /**
     * Associates a course component with a calender ID and an event ID in the Android calendar
     * @param id the course component ID
     * @param calID the Android calendar ID
     * @param eventID the Android event ID
     */
    void associateCourseComponentEvent(int id, long calID, long eventID);
}
