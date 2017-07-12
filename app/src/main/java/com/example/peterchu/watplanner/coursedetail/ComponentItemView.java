package com.example.peterchu.watplanner.coursedetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleComponent;
import com.example.peterchu.watplanner.Models.Schedule.ScheduledClass;
import com.example.peterchu.watplanner.Models.Shared.Location;
import com.example.peterchu.watplanner.R;

/**
 * Created by peterchu on 2017-06-19.
 */

class ComponentItemView extends FrameLayout {
    private Location mLocation;
    private String mStartTime;
    private String mEndTime;
    private String[] mInstructors;
    private Integer mEnrollmentCapacity;
    private Integer mEnrollmentTotal;

    public ComponentItemView(Context context,
                             String day,
                             CourseScheduleComponent courseComponent,
                             ScheduledClass scheduledClass) {
        super(context);
        mLocation = scheduledClass.getLocation();
        mStartTime = scheduledClass.getDate().getStartTime();
        mEndTime = scheduledClass.getDate().getEndTime();
        mInstructors = scheduledClass.getInstructors()
                .toArray(new String[scheduledClass.getInstructors().size()]);
        mEnrollmentCapacity = courseComponent.getEnrollmentCapacity();
        mEnrollmentTotal = courseComponent.getEnrollmentTotal();
        FrameLayout rootView = (FrameLayout) inflate(context, R.layout.component_item_view, this);
        TextView typeAndSection = (TextView) rootView.findViewById(R.id.type_and_section);
        typeAndSection.setText(courseComponent.getSection());
        TextView locationTextView = (TextView) rootView.findViewById(R.id.location);
        String locationText = mLocation != null ? String.format("Location: %s, %s",
                mLocation.getBuilding(),
                mLocation.getRoom())
                : "";
        locationTextView.setText(locationText);
        TextView dayTextView = (TextView) rootView.findViewById(R.id.time);
        dayTextView.setText(String.format("%s, %s - %s", day, mStartTime, mEndTime));
        TextView instructor = (TextView) rootView.findViewById(R.id.instructor);
        instructor.setText("Instructor(s): " + TextUtils.join(",", mInstructors));
        TextView enrollment = (TextView) rootView.findViewById(R.id.enrolment);
        String enrollmentRatio = String.format("enrollment: %d/%d", mEnrollmentTotal,
                mEnrollmentCapacity);
        enrollment.setText(enrollmentRatio);
    }

}
