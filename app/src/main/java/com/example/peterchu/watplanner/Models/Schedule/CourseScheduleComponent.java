package com.example.peterchu.watplanner.Models.Schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CourseScheduleComponent {

    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("catalog_number")
    @Expose
    private String catalogNumber;

    @SerializedName("units")
    @Expose
    private Double units;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("note")
    @Expose
    private Object note;

    @SerializedName("class_number")
    @Expose
    private Integer classNumber;

    @SerializedName("section")
    @Expose
    private String section;

    @SerializedName("campus")
    @Expose
    private String campus;

    @SerializedName("associated_class")
    @Expose
    private Integer associatedClass;

    @SerializedName("related_component_1")
    @Expose
    private Object relatedComponent1;

    @SerializedName("related_component_2")
    @Expose
    private Object relatedComponent2;

    @SerializedName("enrollment_capacity")
    @Expose
    private Integer enrollmentCapacity;

    @SerializedName("enrollment_total")
    @Expose
    private Integer enrollmentTotal;

    @SerializedName("waiting_capacity")
    @Expose
    private Integer waitingCapacity;

    @SerializedName("waiting_total")
    @Expose
    private Integer waitingTotal;

    @SerializedName("topic")
    @Expose
    private Object topic;

    @SerializedName("classes")
    @Expose
    private List<ScheduledClass> classes = null;

    @SerializedName("held_with")
    @Expose
    private List<Object> heldWith = null;

    @SerializedName("term")
    @Expose
    private Integer term;

    @SerializedName("academic_level")
    @Expose
    private String academicLevel;

    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;

    public String getSubject() {
        return subject;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public Double getUnits() {
        return units;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getNote() {
        return note;
    }

    public Integer getClassNumber() {
        return classNumber;
    }

    public String getSection() {
        return section;
    }

    public String getCampus() {
        return campus;
    }

    public Integer getAssociatedClass() {
        return associatedClass;
    }

    public Object getRelatedComponent1() {
        return relatedComponent1;
    }

    public Object getRelatedComponent2() {
        return relatedComponent2;
    }

    public Integer getEnrollmentCapacity() {
        return enrollmentCapacity;
    }

    public Integer getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public Integer getWaitingCapacity() {
        return waitingCapacity;
    }

    public Integer getWaitingTotal() {
        return waitingTotal;
    }

    public Object getTopic() {
        return topic;
    }

    public List<ScheduledClass> getScheduledClasses() {
        return classes;
    }

    public List<Object> getHeldWith() {
        return heldWith;
    }

    public Integer getTerm() {
        return term;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
