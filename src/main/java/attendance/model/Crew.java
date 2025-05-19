package attendance.model;

import java.util.ArrayList;
import java.util.List;

public class Crew {
    private final String name;

    private Crew(String name) {
        this.name = name;
    }

    public static Crew from(String name){
        return new Crew(name);
    }

    public Crew getName(String name) {
        return new Crew(name);
    }
}
