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

    public Crew findByName(String name) {
        return crews.get(name); // 불변 객체만 반환하거나 복사하면 안전
    }
}
