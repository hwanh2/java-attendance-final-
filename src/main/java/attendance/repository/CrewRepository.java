package attendance.repository;

import attendance.model.Attendance;
import attendance.model.Crew;

import java.util.HashMap;
import java.util.Map;

public class CrewRepository {
    private final Map<String, Crew> crews = new HashMap<>();

    public void addAttendance(String name, Attendance attendance) {
        if (!crews.containsKey(name)) {
            crews.put(name, new Crew(name));
        }

        Crew crew = crews.get(name);
        crew.addAttendance(attendance);
    }
}
