package com.example.peterchu.watplanner.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.peterchu.watplanner.BaseView;
import com.example.peterchu.watplanner.Calendar.WeekViewCourseEvent;
import com.example.peterchu.watplanner.Models.Course.Course;
import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.Views.Adapters.CourseListAdapter;
import com.example.peterchu.watplanner.coursedetail.CourseDetailActivity;
import com.example.peterchu.watplanner.coursedetail.CourseDetailFragment;
import com.example.peterchu.watplanner.scheduler.ScheduleUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements BaseView<HomePresenter> {

    private static final Pattern courseCodePattern =
            Pattern.compile("(\\w+[a-zA-Z])[\\s]*([1-9]\\w+)");

    HomePresenter homePresenter;
    CourseListAdapter courseAdapter;
    MaterialSearchView searchView;
    WeekView weekView;
    List<List<CourseComponent>> mCourseSchedule;


    @Override
    public void setPresenter(HomePresenter presenter) {
        homePresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCourseSchedule = new ArrayList<>();
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

        weekView = (WeekView) root.findViewById(R.id.weekViewHome);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                return ScheduleUtils.getWeekViewEvents(mCourseSchedule, newMonth);
            }
        });

        // Set an action when any event is clicked.
        weekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                CourseComponent courseComponent = ((WeekViewCourseEvent) event).getCourseComponent();
                homePresenter.onCalendarEventClicked(courseComponent);
            }
        });

        weekView.setFirstDayOfWeek(Calendar.MONDAY);
        weekView.setShowNowLine(false);
        weekView.goToHour(8);

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
                showExportCalendarDialog();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        homePresenter.start();
    }

    public void showExportCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MaterialLightDialogTheme);
        builder.setTitle("Export courses to calendar");

        builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Replace empty list with the configuration of course components for courses
                // that the user selected/SAT solver came up with
                homePresenter.onExportCoursesToCalendar(mCourseSchedule);
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

    public void addCourseCards(List<Course> courses) {
        courseAdapter.addAll(courses);
        courseAdapter.notifyDataSetChanged();
    }

    public void removeCourseCard(Course course) {
        courseAdapter.remove(course);
        courseAdapter.notifyDataSetChanged();
        Snackbar.make(getActivity().findViewById(R.id.container),
                "Course removed",
                Snackbar.LENGTH_SHORT)
                .show();
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

    public void setCourseSchedule(List<List<CourseComponent>> courseSchedule) {
        mCourseSchedule = courseSchedule;
        homePresenter.saveCourses(mCourseSchedule);
        weekView.notifyDatasetChanged();
    }

    public void emptyCourseList() {
        this.courseAdapter.clear();
    }

    public void showConflictFreeAlternativesDialog(
            final CourseComponent course,
            final List<List<CourseComponent>> alternatives) {

        final AlertDialog.Builder builder = homePresenter.createDialogBuilder(getContext(), course, alternatives);

        if (alternatives.size() > 0) {
            builder.setItems(homePresenter.getListOfAlternativeTimes(alternatives), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // todo: 1.) abstract to presenter
                    // todo: 2.) remove LIST from original section
                    // todo: 3.) add LIST of selected section
                    // todo: 4.) refresh calendar
                    // todo: 5.) translate CourseComponent -> Course (ie. get CourseID)
                    List<CourseComponent> selectedCourseList = alternatives.get(i);
                    List<List<CourseComponent>> updatedSchedule = new ArrayList<>();
                    for (List<CourseComponent> componentList : mCourseSchedule) {
                        List<CourseComponent> partialSchedule = new ArrayList<>();
                        for (CourseComponent component : componentList) {
                            if (!isCourseSameGroup(component, course)) {
                                partialSchedule.add(component);
                            }
                        }
                        updatedSchedule.add(partialSchedule);
                    }
                    updatedSchedule.add(selectedCourseList);
                    setCourseSchedule(updatedSchedule);
                    // remove current section (list)
                    // add selected section
                    // refresh calendar
                    builder.create().dismiss();
                }
            });
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });
        builder.show();
    }

    private boolean isCourseSameGroup(CourseComponent prevCourse, CourseComponent currCourse) {
        if (!prevCourse.getSubject().equals(currCourse.getSubject())) return false;
        if (!prevCourse.getClassNumber().equals(currCourse.getClassNumber())) return false;
        if (!prevCourse.getType().equals(currCourse.getType())) return false;
        if (!prevCourse.getCatalogNumber().equals(currCourse.getCatalogNumber())) return false;
        return true;
    }
}
