package com.example.peterchu.watplanner.Models.Schedule;

import com.example.peterchu.watplanner.Models.Shared.Location;

/**
 * Created by Timothy Tong on 6/16/17.
 */

public class CourseComponent {

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
        return String.format("%s %s - %s %s - %s", subject, catalogNumber, type, section, day);
    }
}
