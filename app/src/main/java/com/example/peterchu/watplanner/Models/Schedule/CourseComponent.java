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

/**
 * Created by Timothy Tong on 6/16/17.
 */

public class CourseComponent {
    private static final DateFormat componentDateFormat = new SimpleDateFormat("HH:mm");

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

    private String term;

    private int id;

    private Integer calendarId;

    private Integer eventId;

    private Boolean inCalendar;

    private Integer courseId;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getType() {
        return type;
    }

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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

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

    public String getTerm() {
        return term;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public Boolean getInCalendar() {
        return inCalendar;
    }

    public void setInCalendar(boolean inCalendar) {
        this.inCalendar = inCalendar;
    }

    public String[] getInstructors() {
        return instructors;
    }

    public void setInstructors(String[] instructors) {
        this.instructors = instructors;
    }

    public String toString() {
        return String.format("%d %s %s - %s %s - %s %s~%s: %d",
                id,
                subject,
                catalogNumber,
                type,
                section,
                day,
                startTime,
                endTime,
                inCalendar ? 1 : 0);
    }

    private Calendar getTermStartDate() {
        // Currently just returns the term start date for the summer term, but should lookup
        // the term start date - TODO: need to add API to lookup term information
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

    /**
     * Gets the first calendar event start time of recurring course component event
     * Goes to the first day of the week that the course component is on after the term start date
     * @return the first calendar event start time
     */
    public Calendar getFirstCalendarStartTime() {
        try {
            Calendar cal = this.getTermStartDate();
            while (cal.get(Calendar.DAY_OF_WEEK) != this.getDayOfWeek()) {
                cal.add(Calendar.DATE, 1);
            }
            Date date = componentDateFormat.parse(this.startTime);
            cal.set(Calendar.HOUR_OF_DAY, date.getHours());
            cal.set(Calendar.MINUTE, date.getMinutes());
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Gets the first calendar event end time of recurring course component event
     * Goes to the first day of the week that the course component is on after the term start date
     * @return the first calendar event end time
     */
    public Calendar getFirstCalendarEndTime() {
        try {
            Calendar cal = this.getTermStartDate();
            while (cal.get(Calendar.DAY_OF_WEEK) != this.getDayOfWeek()) {
                cal.add(Calendar.DATE, 1);
            }
            Date date = componentDateFormat.parse(this.endTime);
            cal.set(Calendar.HOUR_OF_DAY, date.getHours());
            cal.set(Calendar.MINUTE, date.getMinutes());
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }

    public List<WeekViewEvent> toWeekViewEvents(int month) {
        List<WeekViewEvent> weekViewEvents = new ArrayList<>();
        try {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.MONTH, month);
            date.set(Calendar.YEAR, 2017);
            date.set(Calendar.DAY_OF_MONTH, 1);

            Integer dayOfWeek = this.getDayOfWeek();
            // hack for current term - should update with term month data at some point
            if (dayOfWeek == null || (month < Calendar.MAY) || (month > Calendar.AUGUST)) {
                return new ArrayList<>();
            }
            while (date.get(Calendar.MONTH) == month) {
                Calendar eventStartDate = this.getFirstCalendarStartTime();
                Calendar eventEndDate = this.getFirstCalendarEndTime();
                if (Integer.valueOf(date.get(Calendar.DAY_OF_WEEK)) == this.getDayOfWeek()) {
                    WeekViewCourseEvent event = new WeekViewCourseEvent(this);

                    eventStartDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    eventStartDate.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                    eventStartDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
                    eventEndDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    eventEndDate.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                    eventEndDate.set(Calendar.MONTH, date.get(Calendar.MONTH));

                    event.setStartTime(eventStartDate);
                    event.setEndTime(eventEndDate);
                    event.setLocation(String.format("%s %s", type, section));
                    event.setName(String.format("%s %s", subject, catalogNumber));
                    weekViewEvents.add(event);
                }
                date.add(Calendar.DATE, 1);
            }

        } catch (Exception ParseException) {
            return new ArrayList<>();
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

    public String getTermEndDate() {
        // TODO: need to actually fetch term info later
        return "20170725";
    }

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseId() { return this.courseId; }
}
