package com.example.peterchu.watplanner.Models;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Timothy Tong on 6/3/17.
 */

public class Course {
    @SerializedName("subject")
    private String subject;
    @SerializedName("catalog_number")
    private String number;
    @SerializedName("units")
    private String credits;
    @SerializedName("title")
    private String title;

    public Course(String subject, String number, String credits, String title) {
        this.subject = subject;
        this.number = number;
        this.credits = credits;
        this.title = title;
    }

    public String getName() {
        return this.subject + " " + this.number;
    }

}
