package com.example.peterchu.watplanner.scheduler;

import android.util.Pair;

import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.data.IDataRepository;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.solution.AllSolutionsRecorder;
import org.chocosolver.solver.search.solution.Solution;
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
    private IDataRepository dataRepository;

    private Map<BoolVar, List<CourseComponent>> sectionMap;
    private Map<BoolVar, CourseComponent> courseMap;

    private List<List<CourseComponent>> currentSchedule;

    public CourseScheduler(IDataRepository dataRepository) {
        this.dataRepository = dataRepository;
        solver = new Solver("Conflict-free Schedules");
        solver.set(new AllSolutionsRecorder(solver));
        sectionMap = new HashMap<>();
        courseMap = new HashMap<>();
        currentSchedule = new ArrayList<>();
    }

    /**
     * This resets the scheduler to a fresh state.
     */
    public void reset() {
        solver = new Solver("Conflict-free Schedules");
        solver.set(new AllSolutionsRecorder(solver));
        sectionMap.clear();
        courseMap.clear();
        currentSchedule.clear();
    }

    /**
     * The core function of this class that attempts to generate conflict-free schedules
     *
     * @return true if a conflict-free schedule was found, false otherwise
     */
    public boolean generateSchedules() throws ParseException, ContradictionException {
        reset();

        Set<String> courseIds = dataRepository.getUserCourses();

        List<List<BoolVar>> totalBools = new ArrayList<>();

        // Set constraints for each course
        for (String courseId : courseIds) {
            int id = Integer.parseInt(courseId);

            // Lists are already sorted
            List<List<CourseComponent>> lectures = dataRepository.getLectures(id);
            List<List<CourseComponent>> tutorials = dataRepository.getTutorials(id);
            List<List<CourseComponent>> labs = dataRepository.getLabs(id);
            List<List<CourseComponent>> seminars = dataRepository.getSeminars(id);

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
        Set<Pair<BoolVar, BoolVar>> conflicts = generateConflicts(totalBools);
        for (Pair<BoolVar, BoolVar> conflict : conflicts) {
            solver.post(not(and(conflict.first, conflict.second)));
        }

        // Look for a conflict-free schedule
        if (solver.findAllSolutions() > 0) {
            Solution solution = solver.getSolutionRecorder().getLastSolution();
            solver.getSearchLoop().restoreRootNode();
            solver.getEnvironment().worldPush();
            solution.restore(solver);
            for (Map.Entry<BoolVar, List<CourseComponent>> entry : sectionMap.entrySet()) {
                if (entry.getKey().getValue() == 1) {
                    currentSchedule.add(entry.getValue());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public List<List<CourseComponent>> getCurrentSchedule() {
        return currentSchedule;
    }

    /**
     * Given a course component, this will determine what other section that the component can
     * switch into and still maintain a conflict-free schedule.
     */
    public List<List<CourseComponent>> getAlternateSections(CourseComponent component)
            throws ContradictionException {
        List<List<CourseComponent>> ret = new ArrayList<>();
        for (Solution solution : solver.getSolutionRecorder().getSolutions()) {
            solver.getSearchLoop().restoreRootNode();
            solver.getEnvironment().worldPush();
            solution.restore(solver);
            for (Map.Entry<BoolVar, List<CourseComponent>> entry : sectionMap.entrySet()) {
                List<CourseComponent> c = entry.getValue();
                if (entry.getKey().getValue() == 1
                        && isSameCourse(component, c.get(0))
                        && !component.getSection().equals(c.get(0).getSection())) {
                    ret.add(c);
                }
            }
        }
        return ret;
    }

    /**
     * Checks two course component's subject, catalog number, and type to determine if components
     * are for the same course.
     */
    private boolean isSameCourse(CourseComponent a, CourseComponent b) {
        return a.getSubject().equals(b.getSubject())
                && a.getCatalogNumber().equals(b.getCatalogNumber())
                && a.getType().equals(b.getType());
    }

    /**
     * Adds constraints to the solver given a set of components for a SINGLE course.
     * This method also maps each section BoolVar to its corresponding set of components so that
     * when the solution is found, we can look up the course components that are part of the
     * conflict-free schedule.
     */
    private List<List<BoolVar>> addConstraints(List<List<CourseComponent>> components)
            throws ParseException {
        List<List<BoolVar>> sectionList = mapCourseComponents(components);

        if (sectionList.size() == 1) {
            List<BoolVar> sectionA = sectionList.get(0);
            BoolVar[] a = sectionA.toArray(new BoolVar[sectionA.size()]);
            Constraint constraint = and(a);
            sectionMap.put(constraint.reif(), components.get(0));
            solver.post(constraint);
        } else if (sectionList.size() > 1) {
            Constraint total = null;
            for (int i = 0; i < sectionList.size() - 1; i++) {
                List<BoolVar> sectionA = sectionList.get(i);
                BoolVar[] a = sectionA.toArray(new BoolVar[sectionA.size()]);
                Constraint left = and(a);
                solver.post(xor(left, not(or(a))));
                sectionMap.put(left.reif(), components.get(i));
                for (int j = i + 1; j < sectionList.size(); j++) {
                    List<BoolVar> sectionB = sectionList.get(j);
                    BoolVar[] b = sectionB.toArray(new BoolVar[sectionB.size()]);
                    Constraint right = and(b);

                    // Build up a constraint that we want at least one section in the solution
                    if (total == null) {
                        total = or(left, right);
                    } else {
                        total = or(total, or(left, right));
                    }

                    // We constraint that we cannot have both sections in the solution
                    solver.post(not(and(left, right)));

                    // Add last grouping
                    if ((j == sectionList.size() - 1) && (i == j - 1)) {
                        sectionMap.put(right.reif(), components.get(j));
                        solver.post(xor(right, not(or(b))));
                    }
                }
            }

            // Finally post the large OR constraint
            solver.post(total);
        }

        return sectionList;
    }

    /**
     * Checks if pairs of course components conflict and generates a conflict pair.
     * Representation stays as BoolVar so the SAT solver can cleanly accept the object.
     */
    private Set<Pair<BoolVar, BoolVar>> generateConflicts(List<List<BoolVar>> allComponents)
            throws ParseException {
        Set<Pair<BoolVar, BoolVar>> conflicts = new HashSet<>();

        // First two loops grab components associated with a specific course
        for (int i = 0; i < allComponents.size() - 1; i++) {
            List<BoolVar> courseA = allComponents.get(i);
            for (int j = i + 1; j < allComponents.size(); j++) {
                List<BoolVar> courseB = allComponents.get(j);

                // Check for conflicts between two courses
                for (BoolVar boolA : courseA) {
                    CourseComponent classA = courseMap.get(boolA);
                    for (BoolVar boolB : courseB) {
                        CourseComponent classB = courseMap.get(boolB);
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
     * Maps BoolVars to their respective CourseComponents
     */
    private List<List<BoolVar>> mapCourseComponents(List<List<CourseComponent>> componentsList) {
        List<List<BoolVar>> result = new ArrayList<>();
        for (List<CourseComponent> components : componentsList) {
            List<BoolVar> boolVars = new ArrayList<>();
            for (CourseComponent component : components) {
                BoolVar boolVar = VariableFactory.bool(component.toString(), solver);
                boolVars.add(boolVar);
                courseMap.put(boolVar, component);
            }
            result.add(boolVars);
        }
        return result;
    }

    private Constraint xor(Constraint left, Constraint right) {
        return and(or(left, right), not(and(left, right)));
    }
}
