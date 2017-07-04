package com.example.peterchu.watplanner.Calendar;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;

/**
 * Created by peterchu on 2017-07-04.
 */

public class WeekViewCourseEvent extends WeekViewEvent {
    private CourseComponent mCourseComponent;
    public WeekViewCourseEvent(CourseComponent courseComponent) {
        super();
        mCourseComponent = courseComponent;
    }

    public CourseComponent getCourseComponent() {
        return mCourseComponent;
    }
}
