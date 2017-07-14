package com.example.peterchu.watplanner.scheduler;

import android.util.Log;
import android.util.Pair;

import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.data.DataRepository;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.VariableFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.chocosolver.solver.constraints.LogicalConstraintFactory.and;
import static org.chocosolver.solver.constraints.LogicalConstraintFactory.not;
import static org.chocosolver.solver.constraints.LogicalConstraintFactory.or;

/**
 * Handles determining if courses conflict and generating a set of conflict-free schedules through
 * the usage of Choco Solver
 */
public class CourseScheduler {

    private static final DateFormat componentDateFormat = new SimpleDateFormat("HH:mm");

    private Solver solver;
    private DataRepository dataRepository;
    private Map<BoolVar, CourseComponent> boolToComponent;

    public CourseScheduler(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        solver = new Solver("Conflict-free Schedules");
        boolToComponent = new HashMap<>();
    }

    public void generateSchedules() throws ParseException {
        Set<String> courseIds = dataRepository.getUserCourses();

        List<List<BoolVar>> totalBools = new ArrayList<>();

        // Set constraints for each course
        for (String courseId : courseIds) {
            int id = Integer.parseInt(courseId);

            // List is already sorted
            List<CourseComponent> lectures = dataRepository.getLectures(id);
            List<CourseComponent> tutorials = dataRepository.getTutorials(id);
            List<CourseComponent> labs = dataRepository.getLabs(id);
            List<CourseComponent> seminars = dataRepository.getSeminars(id);

            List<BoolVar> subTotal = new ArrayList<>();

            if (lectures.size() > 0) {
                for (List<BoolVar> subList : addConstraints(lectures)) subTotal.addAll(subList);
            }

            if (tutorials.size() > 0) {
                for (List<BoolVar> subList : addConstraints(tutorials)) subTotal.addAll(subList);
            }

            if (labs.size() > 0) {
                for (List<BoolVar> subList : addConstraints(labs)) subTotal.addAll(subList);
            }

            if (seminars.size() > 0) {
                for (List<BoolVar> subList : addConstraints(seminars)) subTotal.addAll(subList);
            }

            totalBools.add(subTotal);
        }

        // Look for conflicts and tell solver to not include those conflicting pairs in solution
        for (Pair<BoolVar, BoolVar> conflict : generateConflicts(totalBools)) {
            solver.post(not(and(conflict.first, conflict.second)));
        }

        if (solver.findSolution()) {
            do {
                Log.d("test", "");
            } while (solver.nextSolution());
        }
    }

    /**
     * Adds constraints to the solver
     */
    private List<List<BoolVar>> addConstraints(List<CourseComponent> lectures)
            throws ParseException {
        List<List<BoolVar>> sectionList = getBoolListsBySection(lectures);

        if (sectionList.size() == 1) {
            List<BoolVar> sectionA = sectionList.get(0);
            BoolVar[] a = sectionA.toArray(new BoolVar[sectionA.size()]);
            solver.post(and(a));
        } else if (sectionList.size() > 1) {
            for (int i = 0; i < sectionList.size() - 1; i++) {
                List<BoolVar> sectionA = sectionList.get(i);
                BoolVar[] a = sectionA.toArray(new BoolVar[sectionA.size()]);
                Constraint left = and(a);
                Constraint negLeft = not(or(a));
                for (int j = i + 1; j < sectionList.size(); j++) {
                    List<BoolVar> sectionB = sectionList.get(j);
                    BoolVar[] b = sectionB.toArray(new BoolVar[sectionB.size()]);
                    Constraint right = and(b);
                    Constraint negRight = not(or(b));
                    solver.post(or(and(left, negRight), and(right, negLeft)));
                }
            }
        }

        return sectionList;
    }

    /**
     * Checks if pairs of course components conflict and generates a conflict pair.
     * Representation stays as BoolVar so the SAT solver can cleanly accept the object.
     */
    private Set<Pair<BoolVar, BoolVar>> generateConflicts(List<List<BoolVar>> components)
            throws ParseException {
        Set<Pair<BoolVar, BoolVar>> conflicts = new HashSet<>();

        // First two loops grab components associated with a specific course
        for (int i = 0; i < components.size() - 1; i++) {
            List<BoolVar> courseA = components.get(i);
            for (int j = i + 1; j < components.size(); j++) {
                List<BoolVar> courseB = components.get(j);

                // Check for conflicts between two courses
                for (BoolVar boolA : courseA) {
                    CourseComponent classA = boolToComponent.get(boolA);
                    for (BoolVar boolB : courseB) {
                        CourseComponent classB = boolToComponent.get(boolB);
                        if (hasConflict(classA, classB)) {
                            conflicts.add(new Pair<>(boolA, boolB));
                        }
                    }
                }
            }
        }

        return conflicts;
    }

    /**
     * Checks if two classes overlap each others. Conflicts if:
     * 1. end time of class A is after start time of class B
     * 2. start time of class A is before end time of class B
     */
    private boolean hasConflict(CourseComponent classA, CourseComponent classB)
            throws ParseException {
        Date startA = componentDateFormat.parse(classA.getStartTime());
        Date endA = componentDateFormat.parse(classA.getEndTime());
        Date startB = componentDateFormat.parse(classB.getStartTime());
        Date endB = componentDateFormat.parse(classB.getEndTime());

        return !(startA.getTime() > endB.getTime()
                || endA.getTime() < startB.getTime()
                || !classA.getDay().equals(classB.getDay()));
    }

    /**
     * Groups together course components by section into a list of lists. This is for easier
     * section by section constraint generation.
     *
     * @param components MUST BE FOR A SINGLE COURSE
     */
    private List<List<BoolVar>> getBoolListsBySection(List<CourseComponent> components) {
        List<List<BoolVar>> result = new ArrayList<>();
        if (components.isEmpty()) return result;

        CourseComponent currentComponent = components.get(0);
        String currentSection = currentComponent.getSection();
        List<BoolVar> currentList = new ArrayList<>();
        BoolVar currentVar = VariableFactory.bool(currentComponent.toString(), solver);
        boolToComponent.put(currentVar, currentComponent);
        currentList.add(currentVar);

        for (int i = 1; i < components.size(); i++) {
            currentComponent = components.get(i);
            if (!currentSection.equals(currentComponent.getSection())) {
                currentSection = currentComponent.getSection();
                result.add(currentList);
                currentList = new ArrayList<>();
            }

            currentVar = VariableFactory.bool(currentComponent.toString(), solver);
            boolToComponent.put(currentVar, currentComponent);
            currentList.add(currentVar);
        }

        if (!currentList.isEmpty()) result.add(currentList);
        return result;
    }

}
