package com.example.peterchu.watplanner.Models.Schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peterchu on 2017-06-11.
 */

public class CourseScheduleResponse {
    @SerializedName("data")
    @Expose
    private List<CourseScheduleComponent> data;

    public List<CourseScheduleComponent> getData() {
        return data;
    }

    public void setData(List<CourseScheduleComponent> data) {
        this.data = data;
    }

}
