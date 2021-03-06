package com.example.peterchu.watplanner.Models.Schedule;

import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peterchu on 2017-06-11.
 */

public class CourseScheduleResponse {
    @SerializedName("data")
    @Expose
    private List<CourseSchedule> data;

    public List<CourseSchedule> getData() {
        return data;
    }

    public void setData(List<CourseSchedule> data) {
        this.data = data;
    }

}
