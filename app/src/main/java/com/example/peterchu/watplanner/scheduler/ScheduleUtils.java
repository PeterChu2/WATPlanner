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
}
