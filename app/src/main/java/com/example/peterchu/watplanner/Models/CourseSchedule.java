package com.example.peterchu.watplanner.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peterchu on 2017-06-11.
 */

public class CourseSchedule {

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

    public Object getNote() {
        return note;
    }

    public void setNote(Object note) {
        this.note = note;
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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Integer getAssociatedClass() {
        return associatedClass;
    }

    public void setAssociatedClass(Integer associatedClass) {
        this.associatedClass = associatedClass;
    }

    public Object getRelatedComponent1() {
        return relatedComponent1;
    }

    public void setRelatedComponent1(Object relatedComponent1) {
        this.relatedComponent1 = relatedComponent1;
    }

    public Object getRelatedComponent2() {
        return relatedComponent2;
    }

    public void setRelatedComponent2(Object relatedComponent2) {
        this.relatedComponent2 = relatedComponent2;
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

    public Object getTopic() {
        return topic;
    }

    public void setTopic(Object topic) {
        this.topic = topic;
    }

    public List<Object> getHeldWith() {
        return heldWith;
    }

    public void setHeldWith(List<Object> heldWith) {
        this.heldWith = heldWith;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
