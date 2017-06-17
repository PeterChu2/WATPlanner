package com.example.peterchu.watplanner.Models.Schedule;

import java.util.List;

/**
 * Created by Timothy Tong on 6/16/17.
 */

public class CourseComponent {

    private ScheduledClass.Location location;

    private String startTime;

    private String endTime;

    private String day;

    private Boolean isTba;

    private Boolean isCancelled;

    private Boolean isClosed;

    private List<String> instructors = null;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Boolean getIsTba() {
        return isTba;
    }

    public void setIsTba(Boolean isTba) {
        this.isTba = isTba;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public ScheduledClass.Location getLocation() {
        return location;
    }

    public void setLocation(ScheduledClass.Location location) {
        this.location = location;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<String> instructors) {
        this.instructors = instructors;
    }

}
