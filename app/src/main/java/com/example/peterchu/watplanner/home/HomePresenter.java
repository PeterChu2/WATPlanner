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
import android.widget.Toast;

import com.example.peterchu.watplanner.BasePresenter;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.data.DataRepository;
import com.example.peterchu.watplanner.data.IDataRepository;
import com.example.peterchu.watplanner.scheduler.CourseScheduler;

import org.chocosolver.solver.exception.ContradictionException;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

class HomePresenter implements BasePresenter {

    private HomeFragment homeFragment;
    private IDataRepository dataRepository;
    private CourseScheduler scheduler;
    private Activity parentActivity;

    public HomePresenter(HomeFragment homeFragment,
                         IDataRepository dataRepository,
                         Activity parentActivity) {
        this.homeFragment = homeFragment;
        this.dataRepository = dataRepository;
        this.parentActivity = parentActivity;
        scheduler = new CourseScheduler(dataRepository);
        homeFragment.setPresenter(this);
    }

    @Override
    public void start() {
        // If data is already synced, the callback executes immediately.
        dataRepository.syncData(new DataRepository.SyncDataCallback() {
            @Override
            public void onDataSynced() {
                loadCourseCards();
                generateScheduleForCalendar();
            }

            @Override
            public void onDataSyncFailure() {
                // TODO: Implement retry. Continual failure should alert user.
            }
        },
        this.parentActivity);
    }

    /**
     * Tells the scheduler to run it's SAT solver to find a conflict-free schedule and the View
     * to display it.
     */
    private void generateScheduleForCalendar() {
        try {
            if (scheduler.generateSchedules()) {
                Log.d("HomePresenter", "Conflict-free schedule generated!");
            } else {
                // The application should never enter this state, throw RTE
                throw new IllegalStateException("No conflict-free schedule generated!");
            }
        } catch (Exception e) {
            Log.e("HomePresenter", "Failed to generate schedule: " + e);
            return;
        }
        homeFragment.setCourseSchedule(scheduler.getCurrentSchedule());
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

    public void onCalendarEventClicked(CourseComponent courseComponent) {
        List<List<CourseComponent>> alternatives;
        try {
            alternatives = scheduler.getAlternateSections(courseComponent);
        } catch (Exception e) {
            Log.e("HomePresenter", "Failed to get alternate slots!");
            return;
        }

        if (alternatives.size() == 0) {
            // No other sections that this course can switch into
        } else {
            homeFragment.showConflictFreeAlternativesDialog(courseComponent, alternatives);
        }
    }
}
