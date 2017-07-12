package com.example.peterchu.watplanner.util;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.Calendar.WeekViewCourseEvent;
import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleComponent;
import com.example.peterchu.watplanner.Models.Schedule.ScheduledClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleUtils {

    private static final String[] WEEKDAYS = {"M", "Th", "W", "T", "F"}; // Th must come before T.

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

    // String is in format TTh, MF, etc.
    public static Set<String> tokenizeDays (String str) {
        Set<String> days = new HashSet<String>();
        if (str.length() == 0) {
            return days;
        }
        for (String day : WEEKDAYS) {
            if (str.contains(day)) {
                days.add(day);
            }
            str = str.replaceFirst(day, "");
        }
        return days;
    }

    public static List<WeekViewEvent> toWeekViewEvents(
            CourseScheduleComponent component, int month) {
        // TODO: hack for current term - should update with term month data at some point
        if ((month < Calendar.MAY) || (month > Calendar.AUGUST)) {
            return new ArrayList<>();
        }

        List<WeekViewEvent> weekViewEvents = new ArrayList<>();
        for (ScheduledClass scheduledClass : component.getScheduledClasses()) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.MONTH, month);
            date.set(Calendar.YEAR, 2017);
            date.set(Calendar.DAY_OF_MONTH, 1);
            try {
                Date eventStartTime = DATE_FORMAT.parse(scheduledClass.getDate().getStartTime());
                Date eventEndTime = DATE_FORMAT.parse(scheduledClass.getDate().getEndTime());
                Set<String> days = tokenizeDays(scheduledClass.getDate().getWeekdays());

                // Iterate through the month to create events
                while (date.get(Calendar.MONTH) == month) {
                    // Only create events that match the day this course is scheduled
                    String currentDay = getDayAsString(date.get(Calendar.DAY_OF_WEEK));
                    if (days.contains(currentDay)) {
                        WeekViewCourseEvent event = new WeekViewCourseEvent(
                                currentDay,
                                component,
                                scheduledClass);
                        Calendar startTime = getCalendarDate(eventStartTime, date);
                        Calendar endTime = getCalendarDate(eventEndTime, date);

                        endTime.set(Calendar.YEAR, date.get(Calendar.YEAR));
                        endTime.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        endTime.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        endTime.set(Calendar.HOUR_OF_DAY, eventEndTime.getHours());
                        endTime.set(Calendar.MINUTE, eventEndTime.getMinutes());

                        event.setStartTime(startTime);
                        event.setEndTime(endTime);
                        event.setLocation(component.getSection().substring(0, 4));
                        event.setName(String.format("%s %s",
                                component.getSubject(),
                                component.getCatalogNumber()));
                        weekViewEvents.add(event);
                    }
                    date.add(Calendar.DATE, 1);
                }

            } catch (Exception ParseException) {
                return new ArrayList<>();
            }
        }

        return weekViewEvents;
    }

    private static String getDayAsString(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return "M";
            case Calendar.TUESDAY:
                return "T";
            case Calendar.WEDNESDAY:
                return "W";
            case Calendar.THURSDAY:
                return "Th";
            case Calendar.FRIDAY:
                return "F";
            case Calendar.SATURDAY:
                return "S";
            case Calendar.SUNDAY:
                return "SU";
            default:
                return null;
        }
    }

    private static Calendar getCalendarDate(Date eventTime, Calendar eventDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, eventDate.get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_MONTH, eventDate.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.MONTH, eventDate.get(Calendar.MONTH));
        cal.set(Calendar.HOUR_OF_DAY, eventTime.getHours());
        cal.set(Calendar.MINUTE, eventTime.getMinutes());
        return cal;
    }
}
