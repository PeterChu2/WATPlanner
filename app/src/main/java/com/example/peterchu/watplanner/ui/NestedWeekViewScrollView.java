package com.example.peterchu.watplanner.ui;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * CUSTOM ScrollView that passes MotionEvents to the WeekView properly.
 * Made for {@link com.example.peterchu.watplanner.coursedetail.CourseDetailFragment}
 */
public class NestedWeekViewScrollView extends NestedScrollView {

    public NestedWeekViewScrollView(Context context) {
        super(context);
    }

    public NestedWeekViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedWeekViewScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                // First view within the container of scrollview is the WeekView card
                LinearLayout container = (LinearLayout) getChildAt(0);
                View view = container.getChildAt(4);
                return (int) ev.getY() > view.getY() && super.onTouchEvent(ev);

            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_UP:
                super.onTouchEvent(ev);
                return false;

            default:
                break;
        }

        return false;
    }
}
