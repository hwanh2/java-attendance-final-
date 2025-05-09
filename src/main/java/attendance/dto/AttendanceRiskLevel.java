package attendance.dto;

public enum AttendanceRiskLevel {
    NORMAL("정상"),
    WARNING("경고"),
    COUNSEL("면담"),
    EXPULSION("제적");

    private final String displayName;

    AttendanceRiskLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
