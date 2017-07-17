package com.example.peterchu.watplanner.home;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.data.DataRepository;
import com.example.peterchu.watplanner.data.IDataRepository;
import com.example.peterchu.watplanner.scheduler.CourseScheduler;
import com.example.peterchu.watplanner.scheduler.ScheduleUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

class HomePresenter implements BasePresenter {

    private HomeFragment homeFragment;
    private IDataRepository dataRepository;
    private CourseScheduler scheduler;
    private Activity parentActivity;
    private List<List<CourseComponent>> schedule;

    public HomePresenter(HomeFragment homeFragment,
                         IDataRepository dataRepository,
                         Activity parentActivity) {
        this.homeFragment = homeFragment;
        this.dataRepository = dataRepository;
        this.parentActivity = parentActivity;
        this.scheduler = new CourseScheduler(dataRepository);
        homeFragment.setPresenter(this);
    }

    @Override
    public void start() {
        // retrieve from cache.
        schedule = dataRepository.getCourseSchedules();
        // If data is already synced, the callback executes immediately.
        dataRepository.syncData(new DataRepository.SyncDataCallback() {
            @Override
            public void onDataSynced() {
                loadCourseCards();
                recoverLastSavedScheduleState();
            }

            @Override
            public void onDataSyncFailure() {
                // TODO: Implement retry. Continual failure should alert user.
            }
        },
        this.parentActivity);
    }

    public void pause() {
        // save to cache.
        dataRepository.setCourseSchedules(schedule);
    }

    /**
     * Tells the scheduler to run it's SAT solver to find a conflict-free schedule and the View
     * to display it.
     */
    private void generateScheduleForCalendar() {
        schedule = ScheduleUtils.getGeneratedSchedules(scheduler);
        homeFragment.setCourseSchedule(schedule);
    }

    private void recoverLastSavedScheduleState() {
        // check cache
        if (schedule != null && schedule.size() > 0) { // cache-hit
            homeFragment.setCourseSchedule(schedule);
        } else { // cache-miss
            generateScheduleForCalendar();
        }
    }

    public void onCourseRemoved(Course course) {
        dataRepository.removeUserCourse(course.getId());
        homeFragment.removeCourseCard(course);
        scheduler.reset();
        generateScheduleForCalendar(); // refresh calendar.
    }

    public void onSearchOpened() {
        homeFragment.addSearchSuggestions(dataRepository.getAllCourses());
    }

    public boolean onSubmitSearchQuery(String subject, String catalogNumber) {
        Course c = dataRepository.getCourse(subject, catalogNumber);
        return homeFragment.openDetailView(c == null ? null : c.getId());
    }

    public void onExportCoursesToCalendar(List<List<CourseComponent>> courseSchedule) {
        boolean canWriteCalendar = ContextCompat.checkSelfPermission(
                this.homeFragment.getActivity(),
                Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED;

        if (!canWriteCalendar) {
            ActivityCompat.requestPermissions(this.homeFragment.getActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 0);
        }

        Toast.makeText(this.homeFragment.getActivity(), "Exporting to calendar ...", Toast.LENGTH_SHORT).show();

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Snackbar.make(homeFragment.getActivity().findViewById(R.id.container),
                            "Courses successfully exported!",
                            Snackbar.LENGTH_SHORT)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        long calId = getDefaultCalendarID(this.homeFragment.getActivity());

        for (List<CourseComponent> courseComponents : courseSchedule) {
            for (CourseComponent courseComponent : courseComponents) {
                if (courseComponent.getCalendarId() != null &&
                        courseComponent.getEventId() != null) {
                    // course component is already exported to user's calendar
                    continue;
                }
                Calendar startDate = courseComponent.getFirstCalendarStartTime();
                Calendar endDate = courseComponent.getFirstCalendarEndTime();
                long startMillis = startDate.getTimeInMillis();
                long endMillis = endDate.getTimeInMillis();

                ContentResolver cr = this.homeFragment.getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, String.format("%s %d %s",
                        courseComponent.getSubject(),
                        Integer.valueOf(courseComponent.getCatalogNumber()),
                        courseComponent.getType()));
                values.put(CalendarContract.Events.DESCRIPTION, String.format("%s with %s",
                        courseComponent.getTitle(),
                        TextUtils.join(",", courseComponent.getInstructors())));
                values.put(CalendarContract.Events.EVENT_LOCATION,
                        courseComponent.getLocation().toString());
                values.put(CalendarContract.Events.CALENDAR_ID, calId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=" + courseComponent
                        .getTermEndDate());

                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                long eventId = Long.parseLong(uri.getLastPathSegment());
                Log.d("CalendarEvent", String.format("Event exported! Calendar ID %d, EventID %d",
                        calId, eventId));
                dataRepository.associateCourseComponentEvent(
                        courseComponent.getId(), calId, eventId);
            }
        }
    }

    public long getDefaultCalendarID(Context c) {
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        if (managedCursor.moveToFirst()) {
            long calID;
            int idCol = managedCursor.getColumnIndex(projection[0]);
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            String name = managedCursor.getString(nameCol);
            do {
                calID = managedCursor.getLong(idCol);
                managedCursor.close();
                Log.d("DefaultCalendar", String.format("%s, %d", name, calID));
                return calID;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        return 1;
    }

    /**
     * Tells the View to display user's saved course in the list as cards
     */
    private void loadCourseCards() {
        homeFragment.emptyCourseList();

        // Load user's saved courses into the list
        final Set<String> savedCourses = dataRepository.getUserCourses();
        if (!savedCourses.isEmpty()) {
            List<Course> courses = dataRepository.getCourses(
                    savedCourses.toArray(new String[savedCourses.size()]));
            homeFragment.addCourseCards(courses);
        }
    }

    public AlertDialog.Builder createDialogBuilder(Context context, CourseComponent component, List<List<CourseComponent>> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MaterialLightDialogTheme);
        if (list.size() == 0) {
            builder.setTitle(String.format("No other sections for this %s", getTypeSpelling(component.getType())));
        } else {
            builder.setTitle(String.format("Switch %s section", getTypeSpelling(component.getType())));
        }
        return builder;
    }

    public CharSequence[] getListOfAlternativeTimes(List<List<CourseComponent>> componentList) {
        CharSequence[] alternativeTimesArray = new CharSequence[componentList.size()];
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < componentList.size(); i++) {
            for (int j = 0; j < componentList.get(i).size(); j++) {
                CourseComponent cc = componentList.get(i).get(j);
                sb.append(deseralizeCourseInfo(getDaySpelling(cc.getDay()), cc.getStartTime(), cc.getEndTime(), cc.getEnrollmentTotal(), cc.getEnrollmentCapacity()));
                sb.append("\n");
            }
            alternativeTimesArray[i] = sb.toString();
            sb.setLength(0); // reset
        }
        return alternativeTimesArray;
    }

    public void onCalendarEventClicked(CourseComponent courseComponent) {
        List<List<CourseComponent>> alternatives;
        try {
            alternatives = scheduler.getAlternateSections(courseComponent);
        } catch (Exception e) {
            Log.e("HomePresenter", "Failed to get alternate slots!");
            return;
        }

        homeFragment.showConflictFreeAlternativesDialog(courseComponent, alternatives);
    }

    public void setAlternativeSchedule(List<CourseComponent> selection) {
        scheduler.setCourseSectionConstraint(selection);
        generateScheduleForCalendar();
    }

    private String getTypeSpelling(String type) {
        switch(type) {
            case "LEC": return "lecture";
            case "TUT": return "tutorial";
            case "LAB": return "lab";
            default: return null;
        }
    }

    private String getDaySpelling(String day) {
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

    private String deseralizeCourseInfo(String day, String start, String end, int total, int capacity) {
        return String.format("%s, %s - %s (%s/%s)", day, start, end, total, capacity);
    }
}
