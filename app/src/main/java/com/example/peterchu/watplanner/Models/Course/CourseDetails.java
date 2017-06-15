package com.example.peterchu.watplanner.Models.Course;

/**
 * Created by peterchu on 2017-06-15.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseDetails {

    @SerializedName("course_id")
    @Expose
    private String courseId;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("catalog_number")
    @Expose
    private String catalogNumber;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("units")
    @Expose
    private Double units;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("instructions")
    @Expose
    private List<String> instructions = null;
    @SerializedName("prerequisites")
    @Expose
    private String prerequisites;
    @SerializedName("antirequisites")
    @Expose
    private String antirequisites;
    @SerializedName("corequisites")
    @Expose
    private String corequisites;
    @SerializedName("crosslistings")
    @Expose
    private Object crosslistings;
    @SerializedName("terms_offered")
    @Expose
    private List<String> termsOffered = null;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("needs_department_consent")
    @Expose
    private Boolean needsDepartmentConsent;
    @SerializedName("needs_instructor_consent")
    @Expose
    private Boolean needsInstructorConsent;
    @SerializedName("extra")
    @Expose
    private List<Object> extra = null;
    @SerializedName("calendar_year")
    @Expose
    private String calendarYear;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("academic_level")
    @Expose
    private String academicLevel;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getUnits() {
        return units;
    }

    public void setUnits(Double units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getAntirequisites() {
        return antirequisites;
    }

    public void setAntirequisites(String antirequisites) {
        this.antirequisites = antirequisites;
    }

    public String getCorequisites() {
        return corequisites;
    }

    public void setCorequisites(String corequisites) {
        this.corequisites = corequisites;
    }

    public Object getCrosslistings() {
        return crosslistings;
    }

    public void setCrosslistings(Object crosslistings) {
        this.crosslistings = crosslistings;
    }

    public List<String> getTermsOffered() {
        return termsOffered;
    }

    public void setTermsOffered(List<String> termsOffered) {
        this.termsOffered = termsOffered;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getNeedsDepartmentConsent() {
        return needsDepartmentConsent;
    }

    public void setNeedsDepartmentConsent(Boolean needsDepartmentConsent) {
        this.needsDepartmentConsent = needsDepartmentConsent;
    }

    public Boolean getNeedsInstructorConsent() {
        return needsInstructorConsent;
    }

    public void setNeedsInstructorConsent(Boolean needsInstructorConsent) {
        this.needsInstructorConsent = needsInstructorConsent;
    }

    public List<Object> getExtra() {
        return extra;
    }

    public void setExtra(List<Object> extra) {
        this.extra = extra;
    }

    public String getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(String calendarYear) {
        this.calendarYear = calendarYear;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

}