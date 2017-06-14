package com.example.peterchu.watplanner.Models.Course;

/**
 * Created by Timothy Tong on 6/5/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseResponse {
    @SerializedName("data")
    private List<Course> data;

    public List<Course> getData() {
        return data;
    }

    public void setData(List<Course> data) {
        this.data = data;
    }

}
