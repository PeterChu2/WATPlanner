package com.example.peterchu.watplanner.Calendar;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleComponent;
import com.example.peterchu.watplanner.Models.Schedule.ScheduledClass;

/**
 * Created by peterchu on 2017-07-04.
 */

public class WeekViewCourseEvent extends WeekViewEvent {

    private String mDay;
    private CourseScheduleComponent mCourseComponent;
    private ScheduledClass mScheduledClass;

    public WeekViewCourseEvent(
            String day,
            CourseScheduleComponent courseComponent,
            ScheduledClass scheduledClass) {
        super();
        mDay = day;
        mCourseComponent = courseComponent;
        mScheduledClass = scheduledClass;
    }

    public CourseScheduleComponent getCourseComponent() {
        return mCourseComponent;
    }

    public ScheduledClass getScheduledClass() {
        return mScheduledClass;
    }

    public String getDay() {
        return mDay;
    }
}
