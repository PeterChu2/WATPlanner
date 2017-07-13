package com.example.peterchu.watplanner.scheduler;

import android.util.Log;
import android.util.Pair;

import com.example.peterchu.watplanner.Models.Schedule.CourseComponent;
import com.example.peterchu.watplanner.data.DataRepository;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.constraints.SatFactory;
import org.chocosolver.solver.constraints.nary.cnf.ILogical;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
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

        // Set constraints for each course
        for (String courseId : courseIds) {
            int id = Integer.parseInt(courseId);

            // List is already sorted
            List<CourseComponent> lectures = dataRepository.getLectures(id);
            List<CourseComponent> tutorials = dataRepository.getTutorials(id);
            List<CourseComponent> labs = dataRepository.getLabs(id);
            List<CourseComponent> seminars = dataRepository.getSeminars(id);

            // Constraint: Only one section for a lecture
            if (lectures.size() > 0) {
                List<List<BoolVar>> lecturesBySection = getBoolListsBySection(lectures);
                for (int i = 0; i < lecturesBySection.size() - 1; i++) {
                    List<BoolVar> sectionA = lecturesBySection.get(i);
                    BoolVar[] a = sectionA.toArray(new BoolVar[sectionA.size()]);
                    Constraint left = and(a);
                    Constraint negLeft = not(or(a));
                    for (int j = i + 1; j < lecturesBySection.size(); j++) {
                        List<BoolVar> sectionB = lecturesBySection.get(j);
                        BoolVar[] b = sectionB.toArray(new BoolVar[sectionB.size()]);
                        Constraint right = and(b);
                        Constraint negRight = not(or(b));
                        solver.post(or(and(left, negRight), and(right, negLeft)));
                    }
                }

                Set<Pair<BoolVar, BoolVar>> conflicts = generateConflicts(lecturesBySection);

                // Constraint: Don't allow conflicts in solution
                for (Pair<BoolVar, BoolVar> pair : conflicts) {
                    solver.post(not(and(pair.first, pair.second)));
                }
            }
        }

        if (solver.findSolution()) {
            do {
                Log.d("test", "");
            } while (solver.nextSolution());
        }
    }

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
