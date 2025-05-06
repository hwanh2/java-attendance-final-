package attendance.model;

import java.util.ArrayList;
import java.util.List;

public class Crew {
    private final String name;
    private final List<Attendance> records;

    public Crew(String name) {
        this.name = name;
        this.records = new ArrayList<>();
    }

    public List<Attendance> getRecords() {
        return records;
    }

}
