package com.example.peterchu.watplanner.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peterchu on 2017-06-11.
 */

public class CourseScheduleResponse {
    @SerializedName("data")
    private List<CourseSchedule> data;

    public List<CourseSchedule> getData() {
        return data;
    }

    public void setData(List<CourseSchedule> data) {
        this.data = data;
    }

}
