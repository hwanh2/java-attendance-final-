package attendance.model;

public enum AttendanceRiskLevel {
    NORMAL("정상",0),
    WARNING("경고",1),
    COUNSEL("면담",2),
    EXPULSION("제적",3);

    private final String displayName;
    private final int priority;


    AttendanceRiskLevel(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriority() {
        return priority;
    }
}