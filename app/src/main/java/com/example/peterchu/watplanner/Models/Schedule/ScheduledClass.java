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

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<String> instructors) {
        this.instructors = instructors;
    }

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

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getWeekdays() {
            return weekdays;
        }

        public void setWeekdays(String weekdays) {
            this.weekdays = weekdays;
        }

        public Object getStartDate() {
            return startDate;
        }

        public void setStartDate(Object startDate) {
            this.startDate = startDate;
        }

        public Object getEndDate() {
            return endDate;
        }

        public void setEndDate(Object endDate) {
            this.endDate = endDate;
        }

        public Boolean getIsTba() {
            return isTba;
        }

        public void setIsTba(Boolean isTba) {
            this.isTba = isTba;
        }

        public Boolean getIsCancelled() {
            return isCancelled;
        }

        public void setIsCancelled(Boolean isCancelled) {
            this.isCancelled = isCancelled;
        }

        public Boolean getIsClosed() {
            return isClosed;
        }

        public void setIsClosed(Boolean isClosed) {
            this.isClosed = isClosed;
        }

    }

}