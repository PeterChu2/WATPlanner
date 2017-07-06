package com.example.peterchu.watplanner.coursedetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.Models.Shared.Location;
import com.example.peterchu.watplanner.R;

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

    public ComponentItemView(Context context, CourseComponent courseComponent) {
        super(context);
        mContext = context;
        mType = courseComponent.getType();
        mSection = courseComponent.getSection();
        mLocation = courseComponent.getLocation();
        mDay = courseComponent.getDay();
        mStartTime = courseComponent.getStartTime();
        mEndTime = courseComponent.getEndTime();
        mInstructors = courseComponent.getInstructors();
        mEnrollmentCapacity = courseComponent.getEnrollmentCapacity();
        mEnrollmentTotal = courseComponent.getEnrollmentTotal();
        FrameLayout rootView = (FrameLayout) inflate(context, R.layout.component_item_view, this);
        TextView typeAndSection = (TextView) rootView.findViewById(R.id.type_and_section);
        typeAndSection.setText(String.format("%s %s", mType, mSection));
        TextView locationTextView = (TextView) rootView.findViewById(R.id.location);
        String locationText = mLocation != null ? String.format("Location: %s, %s", mLocation.getBuilding(),
                mLocation.getRoom())
                : "";
        locationTextView.setText(locationText);
        TextView dayTextView = (TextView) rootView.findViewById(R.id.time);
        dayTextView.setText(String.format("%s, %s - %s", mDay, mStartTime, mEndTime));
        TextView instructor = (TextView) rootView.findViewById(R.id.instructor);
        instructor.setText("Instructor(s): " + TextUtils.join(",", mInstructors));
        TextView enrollment = (TextView) rootView.findViewById(R.id.enrolment);
        String enrollmentRatio = String.format("enrollment: %d/%d", mEnrollmentTotal,
                mEnrollmentCapacity);
        enrollment.setText(enrollmentRatio);
    }

    public ComponentItemView(@NonNull Context context) {
        super(context);
    }

}
