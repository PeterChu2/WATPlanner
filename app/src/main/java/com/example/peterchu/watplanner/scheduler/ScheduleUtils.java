package com.example.peterchu.watplanner.scheduler;

import android.graphics.Color;
import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScheduleUtils {

    private ScheduleUtils() {
        // Util class
    }

    public static List<WeekViewEvent> getWeekViewEvents(List<List<CourseComponent>> allComponents,
                                                        int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();
        // Color each section and type a different color and add to view
        Random rnd = new Random();
        for (List<CourseComponent> components : allComponents) {
            int color = Color.argb(255,
                    rnd.nextInt(256),
                    rnd.nextInt(256),
                    rnd.nextInt(256));
            for (CourseComponent c : components) {
                for (WeekViewEvent event : c.toWeekViewEvents(newMonth)) {
                    event.setColor(color);
                    events.add(event);
                }
            }
        }
        return events;
    }

    public static List<List<CourseComponent>> getGeneratedSchedules(CourseScheduler scheduler) {
        try {
            if (scheduler.generateSchedules()) {
                Log.d("HomePresenter", "Conflict-free schedule generated!");
            } else {
                // The application should never enter this state, throw RTE
                throw new IllegalStateException("No conflict-free schedule generated!");
            }
        } catch (Exception e) {
            Log.e("HomePresenter", "Failed to generate schedule: " + e);
            return new ArrayList<>();
        }
        return scheduler.getCurrentSchedule();
    }

    public static String getTypeSpelling(String type) {
        switch(type) {
            case "LEC": return "lecture";
            case "TUT": return "tutorial";
            case "LAB": return "lab";
            default: return null;
        }
    }

    public static String getDaySpelling(String day) {
        switch (day.toUpperCase()) {
            case "M": return "Monday";
            case "T": return "Tuesday";
            case "W": return "Wednesday";
            case "TH": return "Thursday";
            case "F": return "Friday";
            case "S": return "Saturday";
            case "SU": return "Sunday";
            default: return day;
        }
    }

    public static String deseralizeCourseInfo(String day,
                                               String start,
                                               String end,
                                               int total,
                                               int capacity) {
        return String.format("%s, %s - %s (%s/%s)", day, start, end, total, capacity);
    }
}
