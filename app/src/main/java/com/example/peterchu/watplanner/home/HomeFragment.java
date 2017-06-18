package com.example.peterchu.watplanner.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.peterchu.watplanner.BaseView;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.Views.Adapters.CourseListAdapter;
import com.example.peterchu.watplanner.coursedetail.CourseDetailActivity;
import com.example.peterchu.watplanner.coursedetail.CourseDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements BaseView<HomePresenter> {

    private static final Pattern courseCodePattern =
            Pattern.compile("(\\w+[a-zA-Z])[\\s]*([1-9]\\w+)");

    HomePresenter homePresenter;
    CourseListAdapter courseAdapter;

    @Override
    public void setPresenter(HomePresenter presenter) {
        homePresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseAdapter = new CourseListAdapter(getContext(),
                R.layout.course_list_item_view,
                new ArrayList<Course>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        final ListView listView = (ListView) root.findViewById(R.id.user_courses_list);
        listView.setAdapter(courseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                Intent intent = new Intent(context, CourseDetailActivity.class);
                Course c = (Course) listView.getItemAtPosition(position);
                intent.putExtra(CourseDetailFragment.ARG_COURSE_ID, c.getId());
                context.startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showAddCourseDialog();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        homePresenter.start();
    }

    // TODO: Move adding courses to a new screen
    public void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a course to your schedule");

        final EditText inputEditText = new EditText(getContext());
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputEditText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = inputEditText.getText().toString();
                Matcher matcher = courseCodePattern.matcher(input);
                if (!matcher.find()) {
                    Toast.makeText(getContext(), "Invalid course code!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String subject = matcher.group(1);
                String catalogNumber = matcher.group(2);
                homePresenter.onCourseAdd(subject, catalogNumber);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void addCourses(List<Course> courses) {
        courseAdapter.addAll(courses);
        courseAdapter.notifyDataSetChanged();
    }

}
