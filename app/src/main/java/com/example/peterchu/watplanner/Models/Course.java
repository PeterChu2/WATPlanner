package com.example.peterchu.watplanner.Models;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Timothy Tong on 6/3/17.
 */

public class Course {
    private int id;
    @SerializedName("subject")
    private String subject;
    @SerializedName("catalog_number")
    private String number;
    @SerializedName("units")
    private String credits;
    @SerializedName("title")
    private String title;

    public Course(String subject, String number, String credits, String title) {
        this.id = -1;
        this.subject = subject;
        this.number = number;
        this.credits = credits;
        this.title = title;
    }

    public String getName() {
        return this.subject + " " + this.number;
    }
    public String getTitle() {
        return this.title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getNumber() {
        return this.number;
    }

    public String getCredits() {
        return this.credits;
    }

    public String getTitle() {
        return this.title;
    }

}
