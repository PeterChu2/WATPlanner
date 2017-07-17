package com.example.peterchu.watplanner.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Models.Schedule.ScheduledClass;
import com.example.peterchu.watplanner.Models.Shared.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Timothy Tong on 6/11/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String SERIALIZE_SEPARATOR = "_@@_";
    private static final String[] WEEKDAYS = {"M", "Th", "W", "T", "F"}; // Th must come before T.

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "coursesManager";
    private static final String TABLE_COURSES = "courses";
    private static final String TABLE_SCHEDULES = "schedules";

    // Shared
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_NUMBER = "number";

    // Courses Table Columns names
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_TITLE = "title";

    // Courses Schedule Columns names
    private static final String KEY_CLASS_NUMBER = "classNumber";
    private static final String KEY_TYPE = "type"; // LEC / LAB / TUT
    private static final String KEY_SECTION_NUMBER = "sectionNumber";
    private static final String KEY_ENROLLMENT_CAPACITY = "enrollmentCapacity";
    private static final String KEY_ENROLLMENT_TOTAL = "enrollmentTotal";
    private static final String KEY_WAITING_CAPACITY = "waitingCapacity";
    private static final String KEY_WAITING_TOTAL = "waitingTotal";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_IS_CANCELLED = "isCancelled";
    private static final String KEY_IS_CLOSED = "isClosed";
    private static final String KEY_IS_TBA = "isTba";
    private static final String KEY_DAY = "day";
    private static final String KEY_BUIDING = "building";
    private static final String KEY_ROOM = "room";
    private static final String KEY_COURSE_ID = "courseId";
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_INSTRUCTORS = "instructors";
    private static final String KEY_TERM = "term";
    private static final String KEY_CALENDAR_ID = "calendarId";
    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_IN_CALENDAR = "inCalendar";

    private static DatabaseHandler dbSingleton;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (dbSingleton == null) {
            dbSingleton = new DatabaseHandler(context);
        }
        return dbSingleton;
    }

    public void associateCourseComponentEvent(int id, long calId, long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_SCHEDULES
                + " SET " + KEY_EVENT_ID + " = ?" + ", " + KEY_CALENDAR_ID + " = ?"
                + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(query,
                new String[] {String.valueOf(eventId), String.valueOf(calId), String.valueOf(id)});
        cursor.moveToFirst();
        cursor.close();
        Log.d("CalendarEvent", String.format("Associating course component with ID %d to calendar" +
        "with ID %d with event ID %d", id, calId, eventId));
    }


    private class AddCourseHelper implements Runnable {
        private List<Course> courses;
        DatabaseHandler dbHandler;
        DBHandlerCallback callback;

        public AddCourseHelper(List<Course> courses, DatabaseHandler dbHandler, DBHandlerCallback callback) {
            this.courses = courses;
            this.dbHandler = dbHandler;
            this.callback = callback;
        }

        @Override
        public void run() {
            SQLiteDatabase db = this.dbHandler.getWritableDatabase();

            for (Course course : this.courses) {
                ContentValues values = new ContentValues();
                values.put(KEY_SUBJECT, course.getSubject());
                values.put(KEY_NUMBER, course.getNumber());
                values.put(KEY_CREDITS, course.getCredits());
                values.put(KEY_TITLE, course.getTitle());
                db.insert(TABLE_COURSES, null, values);
            }

            this.callback.onFinishTransaction(this.dbHandler);
        }
    }

    private class AddScheduleHelper implements Runnable {
        private List<CourseSchedule> schedules;
        private DatabaseHandler dbHandler;
        private DBHandlerCallback callback;
        private Course course;

        public AddScheduleHelper(Course course, List<CourseSchedule> schedules, DatabaseHandler dbHandler, DBHandlerCallback callback) {
            this.schedules = schedules;
            this.dbHandler = dbHandler;
            this.callback = callback;
            this.course = course;
        }

        @Override
        public void run() {
            SQLiteDatabase db = this.dbHandler.getWritableDatabase();

            Set<String> componentSectionDayCombos = new HashSet<>();
            for (CourseSchedule courseSchedule : this.schedules) {
                ContentValues values = new ContentValues();

                values.put(KEY_CLASS_NUMBER, courseSchedule.getClassNumber());
                values.put(KEY_SUBJECT, courseSchedule.getSubject());
                values.put(KEY_NUMBER, courseSchedule.getCatalogNumber());
                values.put(KEY_TITLE, courseSchedule.getTitle());
                values.put(KEY_ENROLLMENT_CAPACITY, courseSchedule.getEnrollmentCapacity());
                values.put(KEY_ENROLLMENT_TOTAL, courseSchedule.getEnrollmentTotal());
                values.put(KEY_WAITING_CAPACITY, courseSchedule.getWaitingCapacity());
                values.put(KEY_WAITING_TOTAL, courseSchedule.getWaitingTotal());
                values.put(KEY_COURSE_ID, course.getId());

                String[] typeSection = courseSchedule.getSection().split(" ");
                values.put(KEY_TYPE, typeSection[0]);
                values.put(KEY_SECTION_NUMBER, typeSection[1]);

                // Date
                List<ScheduledClass> scheduledClasses = courseSchedule.getScheduledClasses();
                for (ScheduledClass sClass : scheduledClasses) {
                    ScheduledClass.Date d = sClass.getDate();
                    if (d.getIsTba() || d.getIsCancelled() || d.getIsClosed()) continue;

                    String str = d.getWeekdays() == null ? "" : d.getWeekdays();
                    Set<String> componentDays = tokenizeDays(str);
                    for (String weekday : componentDays) {
                        String componentSectionDayCombo = String.format("%s%s", typeSection, weekday).toUpperCase();
                        if (componentSectionDayCombos.contains(componentSectionDayCombo)) {
                            continue;
                        }
                        componentSectionDayCombos.add(componentSectionDayCombo);
                        values.put(KEY_START_TIME, d.getStartTime());
                        values.put(KEY_END_TIME, d.getEndTime());
                        values.put(KEY_IS_CANCELLED, d.getIsCancelled() ? 1 : 0);
                        values.put(KEY_IS_CLOSED, d.getIsClosed() ? 1 : 0);
                        values.put(KEY_IS_TBA, d.getIsTba() ? 1 : 0);
                        values.put(KEY_DAY, weekday);
                        values.put(KEY_BUIDING, sClass.getLocation().getBuilding());
                        values.put(KEY_ROOM, sClass.getLocation().getRoom());
                        values.put(KEY_INSTRUCTORS, ListToSerializableString(sClass.getInstructors()));
                        values.put(KEY_TERM, courseSchedule.getTerm());
                        values.put(KEY_CALENDAR_ID, (Integer) null);
                        values.put(KEY_EVENT_ID, (Integer) null);
                        values.put(KEY_IN_CALENDAR, 0);
                        Log.d("DBHandler", "Adding Schedules for " + courseSchedule.getSubject() + courseSchedule.getCatalogNumber() + " - " + weekday);
                        db.insert(TABLE_SCHEDULES, null, values);
                    }
                }
            }

            this.callback.onFinishTransaction(this.dbHandler);
        }
    }

    // Helper
    private static String ListToSerializableString(List<String> in) {
        StringBuilder sb = new StringBuilder();
        for (String s : in) {
            sb.append(s + SERIALIZE_SEPARATOR);
        }
        if (in.size() > 0) sb.delete(sb.length() - SERIALIZE_SEPARATOR.length(), sb.length());
        return sb.toString();
    }

    private static String[] SerializableStringToArray(String in) {
        return in.split(SERIALIZE_SEPARATOR);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT + " TEXT,"
                + KEY_NUMBER + " TEXT," + KEY_CREDITS + " TEXT," + KEY_TITLE + " TEXT" + ")";
        db.execSQL(CREATE_COURSES_TABLE);

        String CREATE_SCHEDULES_TABLE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CLASS_NUMBER + " INTEGER,"
                + KEY_SUBJECT + " TEXT," + KEY_NUMBER + " TEXT," + KEY_TITLE + " TEXT,"
                + KEY_ENROLLMENT_CAPACITY + " INTEGER," + KEY_ENROLLMENT_TOTAL + " INTEGER,"
                + KEY_WAITING_CAPACITY + " INTEGER," + KEY_WAITING_TOTAL + " INTEGER,"
                + KEY_TYPE + " TEXT," + KEY_SECTION_NUMBER + " TEXT," + KEY_START_TIME + " TEXT,"
                + KEY_END_TIME + " TEXT," + KEY_IS_CANCELLED + " INTEGER," + KEY_IS_CLOSED + " INTEGER,"
                + KEY_IS_TBA + " INTEGER," + KEY_DAY + " TEXT,"
                + KEY_BUIDING + " TEXT," + KEY_ROOM + " TEXT," + KEY_INSTRUCTORS + " TEXT,"
                + KEY_TERM + " TEXT," + KEY_COURSE_ID + " INTEGER," + KEY_CALENDAR_ID + " INTEGER,"
                + KEY_EVENT_ID + " INTEGER," + KEY_IN_CALENDAR + " INTEGER,"
                + " FOREIGN KEY (" + KEY_COURSE_ID + ") REFERENCES "
                + TABLE_COURSES + "(" + KEY_ID + "))";

        db.execSQL(CREATE_SCHEDULES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        onCreate(db);
    }

    public void addCourses(List<Course> courses, DBHandlerCallback callback) {
        Thread t = new Thread(new AddCourseHelper(courses, this, callback));
        try {
            t.start();
        } catch (Exception e) {
            callback.onTransactionFailed(e);
        }
    }

    public void addSchedules(Course course, List<CourseSchedule> schedules, DBHandlerCallback callback) {
        Thread t = new Thread(new AddScheduleHelper(course, schedules, this, callback));
        try {
            t.start();
        } catch (Exception e) {
            callback.onTransactionFailed(e);
        }
    }

    public void addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, course.getSubject());
        values.put(KEY_NUMBER, course.getNumber());
        values.put(KEY_CREDITS, course.getCredits());
        values.put(KEY_TITLE, course.getTitle());

        db.insert(TABLE_COURSES, null, values);
    }

    // Get course by id
    public Course getCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[]{KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }

        Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        course.setId(Integer.parseInt(cursor.getString(0)));
        cursor.close();

        return course;
    }

    // Get courses by list of ids
    public List<Course> getCourses(String[] ids) {
        if (ids.length == 0) {
            return new ArrayList<Course>();
        }
        List<Course> ret = new ArrayList<Course>();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            sb.append('?');
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[]{KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE},
                KEY_ID + " IN (" + sb.toString() + ")",
                ids,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            return new ArrayList<Course>();
        }
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                course.setId(Integer.parseInt(cursor.getString(0)));
                ret.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    /**
     * Returns the course components of the passed in type for a course id.
     *
     * @return A list of lists contaning course components grouped by section
     */
    public List<List<CourseComponent>> getCourseComponentsBySection(int courseId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCHEDULES
                + " WHERE " + KEY_COURSE_ID + "=" + courseId
                + " AND " + KEY_TYPE + "='" + type + "'";
        Cursor cursor = db.rawQuery(query, null);
        Log.d("GET_LECTURES", "GETTING LECTURES");

        return groupBySection(cursor);
    }

    // Get courses by subject
    public List<Course> getCoursesBySubject(String subject) {
        List<Course> ret = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[]{KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE},
                KEY_SUBJECT + " = ?",
                new String[]{subject},
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                course.setId(Integer.parseInt(cursor.getString(0)));
                ret.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    public List<Course> getCoursesByTitleSubstring(String criterion) {
        List<Course> ret = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[]{KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE},
                KEY_ID + " COLLATE utf8_general_ci LIKE ?",
                new String[]{"%" + criterion + "%"},
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                course.setId(Integer.parseInt(cursor.getString(0)));
                ret.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    // Getting All Courses
    public List<Course> getAllCourses() {
        List<Course> ret = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_COURSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                course.setId(Integer.parseInt(cursor.getString(0)));
                ret.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private CourseComponent makeCourseComponent(Cursor cursor) {
        CourseComponent courseComponent = new CourseComponent();
        courseComponent.setId(Integer.parseInt(cursor.getString(0)));
        courseComponent.setClassNumber(cursor.getInt(1));
        courseComponent.setSubject(cursor.getString(2));
        courseComponent.setCatalogNumber(cursor.getString(3));
        courseComponent.setTitle(cursor.getString(4));
        courseComponent.setEnrollmentCapacity(cursor.getInt(5));
        courseComponent.setEnrollmentTotal(cursor.getInt(6));
        courseComponent.setWaitingCapacity(cursor.getInt(7));
        courseComponent.setWaitingTotal(cursor.getInt(8));
        courseComponent.setType(cursor.getString(9));
        courseComponent.setSection(cursor.getString(10));
        courseComponent.setStartTime(cursor.getString(11));
        courseComponent.setEndTime(cursor.getString(12));
        courseComponent.setIsCancelled(cursor.getInt(13) == 1);
        courseComponent.setIsClosed(cursor.getInt(14) == 1);
        courseComponent.setIsTba(cursor.getInt(15) == 1);
        courseComponent.setDay(cursor.getString(16));
        Location loc = new Location();
        loc.setBuilding(cursor.getString(17));
        loc.setRoom(cursor.getString(18));
        courseComponent.setLocation(loc);
        courseComponent.setInstructors(SerializableStringToArray(cursor.getString(19)));
        courseComponent.setTerm(cursor.getString(20));
        courseComponent.setCourseId(Integer.parseInt(cursor.getString(21)));
        if (!cursor.isNull(22)) {
            courseComponent.setCalendarId(cursor.getInt(22));
        }
        if (!cursor.isNull(23)) {
            courseComponent.setEventId(cursor.getInt(23));
        }
        return courseComponent;
    }

    public static List<CourseComponent> makeCourseComponents(CourseSchedule course) {
        List<CourseComponent> result = new ArrayList<>();
        List<ScheduledClass> scheduledClasses = course.getScheduledClasses();

        // Iterate through "classes" (e.g. TUT, LEC, LAB)
        for (ScheduledClass sClass : scheduledClasses) {
            ScheduledClass.Date d = sClass.getDate();

            if (d.getIsTba() || d.getIsCancelled() || d.getIsClosed()) continue;
            String str = d.getWeekdays() == null ? "" : d.getWeekdays();
            Set<String> componentDays = tokenizeDays(str);
            String[] typeSection = course.getSection().split(" ");

            // Create a component for each day this class exists
            for (String weekday : componentDays) {
                CourseComponent courseComponent = new CourseComponent();
                courseComponent.setClassNumber(course.getClassNumber());
                courseComponent.setSubject(course.getSubject());
                courseComponent.setCatalogNumber(course.getCatalogNumber());
                courseComponent.setTitle(course.getTitle());
                courseComponent.setEnrollmentCapacity(course.getEnrollmentCapacity());
                courseComponent.setEnrollmentTotal(course.getEnrollmentTotal());
                courseComponent.setWaitingCapacity(course.getWaitingCapacity());
                courseComponent.setWaitingTotal(course.getWaitingTotal());

                courseComponent.setType(typeSection[0]);
                courseComponent.setSection(typeSection[1]);

                courseComponent.setStartTime(d.getStartTime());
                courseComponent.setEndTime(d.getEndTime());
                courseComponent.setIsCancelled(d.getIsCancelled());
                courseComponent.setIsClosed(d.getIsClosed());
                courseComponent.setIsTba(d.getIsTba());
                courseComponent.setLocation(sClass.getLocation());
                courseComponent.setInstructors(
                        SerializableStringToArray(sClass.getInstructors().toString()));
                courseComponent.setDay(weekday);
                result.add(courseComponent);
            }
        }

        return result;
    }

    public List<CourseComponent> getAllCourseSchedules() {
        List<CourseComponent> ret = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CourseComponent courseComponent = makeCourseComponent(cursor);
                ret.add(courseComponent);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    public List<List<CourseComponent>> getCourseSchedule(String subject, String catalogNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULES + " WHERE " + KEY_SUBJECT + "=\"" + subject
                + "\" AND " + KEY_NUMBER + "=\"" + catalogNumber + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return groupBySection(cursor);
    }

    private List<List<CourseComponent>> groupBySection(Cursor cursor) {
        List<List<CourseComponent>> list = new ArrayList<>();
        List<CourseComponent> currentList = new ArrayList<>();
        String currentSection = null;
        if (cursor.moveToFirst()) {
            do {
                CourseComponent courseComponent = makeCourseComponent(cursor);
                if (currentList.size() > 0
                        && !courseComponent.getSection().equals(currentSection)) {
                    list.add(currentList);
                    currentList = new ArrayList<>();
                }
                currentSection = courseComponent.getSection();
                currentList.add(courseComponent);
            } while (cursor.moveToNext());
            if (currentList.size() > 0) list.add(currentList);
        }
        cursor.close();
        return list;
    }

    public Course getCourseByCourseCode(String subject, String catalogNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[]{KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE},
                KEY_SUBJECT + "=UPPER(?) AND " + KEY_NUMBER + "=UPPER(?)",
                new String[]{subject, catalogNumber},
                null,
                null,
                null,
                null
        );

        if (cursor == null || !cursor.moveToFirst()) return null;

        Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        course.setId(Integer.parseInt(cursor.getString(0)));
        cursor.close();
        return course;
    }

    /**
     * No need for an UPDATE operation.
     **/

    // Deleting single Course
    public void deleteCourse(Course Course) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_ID + " = ?", new String[]{String.valueOf(Course.getId())});
    }

    public void destroyAndRecreateDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        onCreate(db);
    }

    public int getCoursesCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getSchedulesCount() {
        String countQuery = "SELECT * FROM " + TABLE_SCHEDULES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void resetCachedCalendar() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_SCHEDULES
                + " SET " + KEY_IN_CALENDAR + "=0"
                + " WHERE " + KEY_IN_CALENDAR + "=1";
        db.rawQuery(query, null);
    }

    public void saveCalendar(List<List<CourseComponent>> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        resetCachedCalendar();
        for (List<CourseComponent> components: list) {
            for (CourseComponent c: components) {
                String query = "UPDATE " + TABLE_SCHEDULES
                        + " SET " + KEY_IN_CALENDAR + "=1"
                        + " WHERE " + KEY_ID+ "=" + c.getId();
                db.rawQuery(query, null);
            }
        }
    }

    public List<List<CourseComponent>> getCalendar() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<List<CourseComponent>> ret = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SCHEDULES + " WHERE " + KEY_IN_CALENDAR + "=1 ORDER BY "
                + KEY_COURSE_ID + ", " + KEY_TYPE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int prevCourseId = -1;
            String prevType = "";
            List<CourseComponent> components = new ArrayList<>();
            do {
                CourseComponent courseComponent = makeCourseComponent(cursor);
                if (courseComponent.getCourseId() != prevCourseId || !courseComponent.getType().equals(prevType)) {
                    Log.d("GetCalendar", "======= new list =======");
                    ret.add(components);
                    components = new ArrayList<>();
                    prevCourseId = courseComponent.getCourseId();
                    prevType = courseComponent.getType();
                }
                Log.d("GetCalendar", courseComponent.toString());
                components.add(courseComponent);
            } while (cursor.moveToNext());
            ret.add(components);
        }
        cursor.close();
        return ret;
    }

    // string is in format TTh, MF, etc.
    private static Set<String> tokenizeDays(String str) {
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
}
