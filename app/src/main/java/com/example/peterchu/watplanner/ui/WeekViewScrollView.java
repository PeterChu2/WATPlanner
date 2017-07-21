package com.example.peterchu.watplanner.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * CUSTOM ScrollView that passes MotionEvents to the WeekView properly.
 */
public class WeekViewScrollView extends ScrollView {

    public WeekViewScrollView(Context context) {
        super(context);
    }

    public WeekViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekViewScrollView(Context context, AttributeSet attrs, int defStyle) {
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
                View view = container.getChildAt(0);
                if ((int) ev.getY() > view.getHeight()) {
                    super.onTouchEvent(ev);
                    return false;
                }
                return (int) ev.getY() > view.getHeight() && super.onTouchEvent(ev);

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
