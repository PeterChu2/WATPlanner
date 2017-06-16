package com.example.peterchu.watplanner.Models.Course;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by peterchu on 2017-06-15.
 */

public class CourseDetailsResponse {
    @SerializedName("data")
    @Expose
    private CourseDetails data;

    public CourseDetails getData() {
        return data;
    }

    public void setData(CourseDetails data) {
        this.data = data;
    }
}