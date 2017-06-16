package com.example.peterchu.watplanner.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseSchedule;
import com.example.peterchu.watplanner.Models.Schedule.ScheduledClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy Tong on 6/11/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
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
    private static final String KEY_SESSION_NUMBER = "sessionNumber";
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

            db.close();
            this.callback.onFinishTransaction(this.dbHandler);
        }
    }

    private class AddScheduleHelper implements Runnable {
        private List<CourseSchedule> schedules;
        DatabaseHandler dbHandler;
        DBHandlerCallback callback;

        public AddScheduleHelper(List<CourseSchedule> schedules, DatabaseHandler dbHandler, DBHandlerCallback callback) {
            this.schedules = schedules;
            this.dbHandler = dbHandler;
            this.callback = callback;
        }

        @Override
        public void run() {
            SQLiteDatabase db = this.dbHandler.getWritableDatabase();

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

                String[] typeSection = courseSchedule.getSection().split(" ");
                values.put(KEY_TYPE, typeSection[0]);
                values.put(KEY_SESSION_NUMBER, typeSection[1]);

                // Date
                List<ScheduledClass> scheduledClasses = courseSchedule.getScheduledClasses();
                for (ScheduledClass sClass: scheduledClasses) {
                    ScheduledClass.Date d = sClass.getDate();
                    values.put(KEY_START_TIME, d.getStartTime());
                    values.put(KEY_END_TIME, d.getEndTime());
                    values.put(KEY_IS_CANCELLED, d.getIsCancelled() ? 1 : 0);
                    values.put(KEY_IS_CLOSED, d.getIsClosed() ? 1 : 0);
                    values.put(KEY_IS_TBA, d.getIsTba() ? 1 : 0);
                    values.put(KEY_DAY, d.getWeekdays());
                    db.insert(TABLE_COURSES, null, values);
                }

            }

            db.close();
            this.callback.onFinishTransaction(this.dbHandler);
        }
    }


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                + KEY_SUBJECT + " TEXT,"  + KEY_NUMBER + " TEXT," + KEY_TITLE + " TEXT,"
                + KEY_ENROLLMENT_CAPACITY + " INTEGER," + KEY_ENROLLMENT_TOTAL + " INTEGER,"
                + KEY_WAITING_CAPACITY + " INTEGER," + KEY_WAITING_TOTAL + " INTEGER,"
                + KEY_TYPE + " TEXT," + KEY_SESSION_NUMBER + " TEXT," + KEY_START_TIME + " TEXT,"
                + KEY_END_TIME + " TEXT," + KEY_IS_CANCELLED + " INTEGER," + KEY_IS_CLOSED + " INTEGER,"
                + KEY_IS_TBA + " INTEGER," + KEY_DAY + " TEXT" + ")";
        db.execSQL(CREATE_SCHEDULES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES + "," + TABLE_SCHEDULES);
        onCreate(db);
    }

    public void addCourses(List<Course> courses, DBHandlerCallback callback) throws Exception {
        Thread t = new Thread(new AddCourseHelper(courses, this, callback));
        try {
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void addSchedules(List<CourseSchedule> schedules, DBHandlerCallback callback) throws Exception {
        Thread t = new Thread(new AddScheduleHelper(schedules, this, callback));
        try {
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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
        db.close();
    }

    // Get course by id
    public Course getCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null
        );

        if (cursor == null) { return null; }
        cursor.moveToFirst();
        if (cursor.getCount() == 0) { return null; }

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
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
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

    // Get courses by subject
    public List<Course> getCoursesBySubject(String subject) {
        List<Course> ret = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
                KEY_SUBJECT + " = ?",
                new String[] { subject },
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
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
                KEY_ID + " COLLATE utf8_general_ci LIKE ?",
                new String[] { "%" + criterion + "%" },
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

    public List<CourseSchedule> getAllCourseSchedules() {
        List<CourseSchedule> ret = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CourseSchedule courseSchedule = new CourseSchedule();
                courseSchedule.setClassNumber(cursor.getInt(1));
                courseSchedule.setSubject(cursor.getString(2));
                courseSchedule.setCatalogNumber(cursor.getString(3));
                courseSchedule.setTitle(cursor.getString(4));
                courseSchedule.setEnrollmentCapacity(cursor.getInt(5));
                courseSchedule.setEnrollmentTotal(cursor.getInt(6));
                courseSchedule.setWaitingCapacity(cursor.getInt(7));
                courseSchedule.setWaitingTotal(cursor.getInt(8));
                courseSchedule.setType(cursor.getString(9));
                courseSchedule.setSession(cursor.getString(10));
                courseSchedule.setStartTime(cursor.getString(11));
                courseSchedule.setEndTime(cursor.getString(12));
                courseSchedule.setIsCancelled(cursor.getInt(13) == 1 ? true : false);
                courseSchedule.setIsClosed(cursor.getInt(14) == 1 ? true : false);
                courseSchedule.setIsTba(cursor.getInt(15) == 1 ? true : false);
                courseSchedule.setDay(cursor.getString(16));

                ret.add(courseSchedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    public CourseSchedule getCourseSchedule(String subject, String catalogNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_SCHEDULES,
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
                KEY_SUBJECT + "=? AND " + KEY_NUMBER + "=?",
                new String[] { subject, catalogNumber },
                null,
                null,
                null,
                null
        );

        if (cursor == null) { return null; }
        cursor.moveToFirst();
        if (cursor.getCount() == 0) { return null; }

        CourseSchedule courseSchedule = new CourseSchedule();
        courseSchedule.setClassNumber(cursor.getInt(1));
        courseSchedule.setSubject(cursor.getString(2));
        courseSchedule.setCatalogNumber(cursor.getString(3));
        courseSchedule.setTitle(cursor.getString(4));
        courseSchedule.setEnrollmentCapacity(cursor.getInt(5));
        courseSchedule.setEnrollmentTotal(cursor.getInt(6));
        courseSchedule.setWaitingCapacity(cursor.getInt(7));
        courseSchedule.setWaitingTotal(cursor.getInt(8));
        courseSchedule.setType(cursor.getString(9));
        courseSchedule.setSession(cursor.getString(10));
        courseSchedule.setStartTime(cursor.getString(11));
        courseSchedule.setEndTime(cursor.getString(12));
        courseSchedule.setIsCancelled(cursor.getInt(13) == 1 ? true : false);
        courseSchedule.setIsClosed(cursor.getInt(14) == 1 ? true : false);
        courseSchedule.setIsTba(cursor.getInt(15) == 1 ? true : false);
        courseSchedule.setDay(cursor.getString(16));
        cursor.close();
        return courseSchedule;
    }

    public Course getCourseByCourseCode(String subject, String catalogNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_COURSES,
                new String[] { KEY_ID, KEY_SUBJECT, KEY_NUMBER, KEY_CREDITS, KEY_TITLE },
                KEY_SUBJECT + "=UPPER(?) AND " + KEY_NUMBER + "=UPPER(?)",
                new String[] { subject, catalogNumber },
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

    /** No need for an UPDATE operation. **/

    // Deleting single Course
    public void deleteCourse(Course Course) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_ID + " = ?", new String[] { String.valueOf(Course.getId()) });
        db.close();
    }

    public void deleteAllCoures() {
        // Armaggedon
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
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
}
