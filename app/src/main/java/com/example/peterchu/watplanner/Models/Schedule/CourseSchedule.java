package com.example.peterchu.watplanner.Models.Schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseSchedule {

    @SerializedName("data")
    @Expose
    private List<CourseScheduleComponent> classes = null;

    public List<CourseScheduleComponent> getClasses() {
        return classes;
    }
}
