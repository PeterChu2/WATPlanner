package com.example.peterchu.watplanner.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.peterchu.watplanner.BaseView;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.Views.Adapters.CourseListAdapter;
import com.example.peterchu.watplanner.coursedetail.CourseDetailActivity;
import com.example.peterchu.watplanner.coursedetail.CourseDetailFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements BaseView<HomePresenter> {

    private static final Pattern courseCodePattern =
            Pattern.compile("(\\w+[a-zA-Z])[\\s]*([1-9]\\w+)");

    HomePresenter homePresenter;
    CourseListAdapter courseAdapter;
    MaterialSearchView searchView;

    @Override
    public void setPresenter(HomePresenter presenter) {
        homePresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        courseAdapter = new CourseListAdapter(getContext(),
                R.layout.course_list_item_view,
                new ArrayList<Course>(),
                new CourseListAdapter.CourseRemoveClickListener() {
                    @Override
                    public void onCourseRemoveClicked(Course course) {
                        homePresenter.onCourseRemoved(course);
                    }
                });
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
                Course c = (Course) listView.getItemAtPosition(position);
                openDetailView(c.getId());
            }
        });

        searchView = (MaterialSearchView) getActivity()
                .findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setSubmitOnClick(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Matcher matcher = courseCodePattern.matcher(query);
                if (!matcher.find()) {
                    Toast.makeText(getContext(), "Invalid course code!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String subject = matcher.group(1);
                String catalogNumber = matcher.group(2);

                return homePresenter.onSubmitSearchQuery(subject, catalogNumber);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                homePresenter.onSearchOpened();
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
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

    public void removeCourse(Course course) {
        courseAdapter.remove(course);
        courseAdapter.notifyDataSetChanged();
        Snackbar.make(getActivity().findViewById(R.id.container), "Course removed", Snackbar.LENGTH_SHORT).show();
    }

    protected void addSearchSuggestions(List<Course> courses) {
        List<String> courseNames = new ArrayList<String>();
        for (Course course : courses) {
            courseNames.add(course.getName());
        }
        searchView.setSuggestions(courseNames.toArray(new String[courseNames.size()]));
    }

    protected boolean openDetailView(Integer courseId) {
        if (courseId == null) {
            Toast.makeText(getContext(), "Course not found!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Intent intent = new Intent(getContext(), CourseDetailActivity.class);
        Context context = HomeFragment.this.getContext();
        intent.putExtra(CourseDetailFragment.ARG_COURSE_ID, courseId);
        context.startActivity(intent);
        return true;
    }

    public void emptyCourseList() {
        this.courseAdapter.clear();
    }
}
