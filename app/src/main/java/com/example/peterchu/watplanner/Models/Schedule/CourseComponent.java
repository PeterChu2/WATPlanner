package com.example.peterchu.watplanner.Models.Schedule;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.Calendar.WeekViewCourseEvent;
import com.example.peterchu.watplanner.Models.Shared.Location;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Timothy Tong on 6/16/17.
 */

public class CourseComponent {
    private static final DateFormat componentDateFormat = new SimpleDateFormat( "HH:mm");

    private String subject;

    private String catalogNumber;

    private double units;

    private String title;

    private int classNumber;

    private String type;

    private String section;

    private Integer enrollmentCapacity;

    private Integer enrollmentTotal;

    private Integer waitingCapacity;

    private Integer waitingTotal;

    private Location location;

    private String startTime;

    private String endTime;

    private String day;

    private Boolean isTba;

    private Boolean isCancelled;

    private Boolean isClosed;

    private String[] instructors;

    public String getType() { return type; }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public Double getUnits() {
        return units;
    }

    public void setUnits(Double units) {
        this.units = units;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    public String getSection() { return section; }

    public void setSection(String section) { this.section = section; }

    public Integer getEnrollmentCapacity() {
        return enrollmentCapacity;
    }

    public void setEnrollmentCapacity(Integer enrollmentCapacity) {
        this.enrollmentCapacity = enrollmentCapacity;
    }

    public Integer getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public void setEnrollmentTotal(Integer enrollmentTotal) {
        this.enrollmentTotal = enrollmentTotal;
    }

    public Integer getWaitingCapacity() {
        return waitingCapacity;
    }

    public void setWaitingCapacity(Integer waitingCapacity) {
        this.waitingCapacity = waitingCapacity;
    }

    public Integer getWaitingTotal() {
        return waitingTotal;
    }

    public void setWaitingTotal(Integer waitingTotal) {
        this.waitingTotal = waitingTotal;
    }

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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String[] getInstructors() {
        return instructors;
    }

    public void setInstructors(String[] instructors) {
        this.instructors = instructors;
    }

    public String toString() {
        return String.format("%s %s - %s %s - %s %s~%s", subject, catalogNumber, type, section, day, startTime, endTime);
    }

    public List<WeekViewEvent> toWeekViewEvents(int month) {
        List<WeekViewEvent> weekViewEvents = new ArrayList<WeekViewEvent>();
        try {
            Date eventStartTime = componentDateFormat.parse(this.startTime);
            Date eventEndTime = componentDateFormat.parse(this.endTime);
            String eventName = String.format("%s %s %s", subject, catalogNumber, type);

            Calendar date = Calendar.getInstance();
            date.set(Calendar.MONTH, month);
            date.set(Calendar.YEAR, 2017);
            date.set(Calendar.DAY_OF_MONTH, 1);

            Integer dayOfWeek = this.getDayOfWeek();
            // hack for current term - should update with term month data at some point
            if (dayOfWeek == null || (month < Calendar.MAY) || (month > Calendar.AUGUST)) {
                return new ArrayList<WeekViewEvent>();
            }
            while (date.get(Calendar.MONTH) == month) {
                if (Integer.valueOf(date.get(Calendar.DAY_OF_WEEK)) == this.getDayOfWeek()) {
                    WeekViewCourseEvent event = new WeekViewCourseEvent(this);
                    Calendar startTime = getCalendarDate(eventStartTime, date);
                    Calendar endTime = getCalendarDate(eventEndTime, date);

                    endTime.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    endTime.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                    endTime.set(Calendar.MONTH, date.get(Calendar.MONTH));
                    endTime.set(Calendar.HOUR_OF_DAY, eventEndTime.getHours());
                    endTime.set(Calendar.MINUTE, eventEndTime.getMinutes());

                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setLocation(location.toString());
                    event.setName(eventName);
                    weekViewEvents.add(event);
                }
                date.add(Calendar.DATE, 1);
            }

        } catch (Exception ParseException) {
            return new ArrayList<WeekViewEvent>();
        }

        return weekViewEvents;
    }

    private Integer getDayOfWeek() {
        switch (this.day.toUpperCase()) {
            case "M":
                return Calendar.MONDAY;
            case "T":
                return Calendar.TUESDAY;
            case "W":
                return Calendar.WEDNESDAY;
            case "TH":
                return Calendar.THURSDAY;
            case "F":
                return Calendar.FRIDAY;
            case "S":
                return Calendar.SATURDAY;
            case "SU":
                return Calendar.SUNDAY;
            default:
                return null;
        }
    }

    private Calendar getCalendarDate(Date eventTime, Calendar eventDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, eventDate.get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_MONTH, eventDate.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.MONTH, eventDate.get(Calendar.MONTH));
        cal.set(Calendar.HOUR_OF_DAY, eventTime.getHours());
        cal.set(Calendar.MINUTE, eventTime.getMinutes());
        return cal;
    }
}
