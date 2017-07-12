package com.example.peterchu.watplanner.Models.Schedule;

/**
 * Created by peterchu on 2017-06-14.
 */


import com.example.peterchu.watplanner.Models.Shared.Location;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduledClass {

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("instructors")
    @Expose
    private List<String> instructors = null;

    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getInstructors() {
        return instructors;
    }
}