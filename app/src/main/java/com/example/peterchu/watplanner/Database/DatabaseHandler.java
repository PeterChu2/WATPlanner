package com.example.peterchu.watplanner.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.peterchu.watplanner.Models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy Tong on 6/11/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "coursesManager";
    private static final String TABLE_COURSES = "courses";

    // Courses Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_TITLE = "title";

    private class AddCourseHelper implements Runnable {
        private List<Course> courses;
        DatabaseHandler dbHandler;

        public AddCourseHelper(List<Course> courses, DatabaseHandler dbHandler) {
            this.courses = courses;
            this.dbHandler = dbHandler;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }

    public void addCourses(List<Course> courses) throws Exception {
        Thread t = new Thread(new AddCourseHelper(courses, this));
        try {
            t.start();
            //t.join();
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

        if (cursor != null)  cursor.moveToFirst();

        Course course = new Course(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        course.setId(Integer.parseInt(cursor.getString(0)));

        return course;
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

        return ret;
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
