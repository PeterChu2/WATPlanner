package com.example.peterchu.watplanner.coursedetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.R;

import java.util.ArrayList;

public class ConflictResolveItemView extends FrameLayout {

    private String mType;
    private String mDay;
    private String mStartTime;
    private String mEndTime;

    // todo: special logic.
    private int numOfAlternative;
    private boolean isAlternative;

    public ConflictResolveItemView(Context context, CourseComponent courseComponent) {
        super(context);

        mType = courseComponent.getType();
        mDay = courseComponent.getDay();
        mStartTime = courseComponent.getStartTime();
        mEndTime = courseComponent.getEndTime();

        // todo: call SAT solver, currently hardcoded.
        numOfAlternative = 3;
        isAlternative = true;

        FrameLayout rootView = (FrameLayout) inflate(context, R.layout.conflict_resolve_view, this);

        ListView listView = (ListView) rootView.findViewById(R.id.resolution_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.conflict_soln_list_view, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // i = list # clicked.
            }
        });

        TextView info = (TextView) rootView.findViewById(R.id.info);

        if (isAlternative) {
            info.setText(String.format("Switch to another conflict-free slot for this %s", getTypeSpelling(mType)));
            ArrayList<String> options = new ArrayList<>();
            for (int i = 1; i < numOfAlternative; i++) {
                options.add(String.format(i + ".) %s, %s - %s", mDay, mStartTime, mEndTime));
            }
            adapter.addAll(options);
        } else {
            info.setText(String.format("No other conflict-free slots exist for this %s", getTypeSpelling(mType)));
        }
    }

    private String getTypeSpelling(String type) {
        switch(type) {
            case "LEC": return "lecture";
            case "TUT": return "tutorial";
            case "LAB": return "lab";
            default: return null;
        }
    }

    public ConflictResolveItemView(@NonNull Context context) {
        super(context);
    }
}
