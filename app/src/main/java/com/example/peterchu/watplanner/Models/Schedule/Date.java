package com.example.peterchu.watplanner.Models.Schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Date {

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("end_time")
    @Expose
    private String endTime;

    @SerializedName("weekdays")
    @Expose
    private String weekdays;

    @SerializedName("start_date")
    @Expose
    private Object startDate;

    @SerializedName("end_date")
    @Expose
    private Object endDate;

    @SerializedName("is_tba")
    @Expose
    private Boolean isTba;

    @SerializedName("is_cancelled")
    @Expose
    private Boolean isCancelled;

    @SerializedName("is_closed")
    @Expose
    private Boolean isClosed;

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public Object getStartDate() {
        return startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public Boolean getIsTba() {
        return isTba;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }
}
