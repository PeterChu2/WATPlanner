package com.example.peterchu.watplanner.coursedetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Shared.Location;

/**
 * Created by peterchu on 2017-06-19.
 */

class ComponentItemView extends FrameLayout {
    private Context mContext;
    private String mType;
    private String mSection;
    private Location mLocation;
    private String mDay;
    private String mStartTime;
    private String mEndTime;
    private String[] mInstructors;
    private Integer mEnrollmentCapacity;
    private Integer mEnrollmentTotal;

    public ComponentItemView(Context context, String type, String section, Location location,
                             String day, String startTime, String endTime, String[] instructors,
                             Integer enrollmentCapacity, Integer enrollmentTotal) {
        super(context);
        mContext = context;
        mType = type;
        mSection = section;
        mLocation = location;
        mDay = day;
        mStartTime = startTime;
        mEndTime = endTime;
        mInstructors = instructors;
        mEnrollmentCapacity = enrollmentCapacity;
        mEnrollmentTotal = enrollmentTotal;
        FrameLayout rootView = (FrameLayout) inflate(context, R.layout.component_item_view, this);
        TextView type = (TextView) rootView.findViewById(R.id.section_and);
        TextView section = (TextView) rootView.findViewById(R.id.section_and);
        TextView location = (TextView) rootView.findViewById(R.id.section_and);
        TextView day = (TextView) rootView.findViewById(R.id.section_and);
        TextView startTime = (TextView) rootView.findViewById(R.id.section_and);
        TextView endTime = (TextView) rootView.findViewById(R.id.section_and);
        TextView instructor = (TextView) rootView.findViewById(R.id.section_and);
        TextView enrollment = (TextView) rootView.findViewById(R.id.section_and);

        String enrollmentRatio = String.format("%d/%d", enrollmentTotal, enrollmentCapacity);
        enrollment.setText(enrollmentRatio)
    }

    public ComponentItemView(@NonNull Context context) {
        super(context);
    }

}
