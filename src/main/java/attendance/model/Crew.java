package attendance.model;


import java.util.Objects;

public class Crew {
    private final String name;
    private int attendCount = 0;
    private int lateCount = 0;
    private int absentCount = 0;
    private AttendanceRiskLevel riskLevel = AttendanceRiskLevel.NORMAL;

    private Crew(String name) {
        this.name = name;
    }

    public static Crew from(String name){
        return new Crew(name);
    }

    public String getName(){
        return name;
    }

    public void applyAttendanceStatus(AttendanceStatus status) {
        if(status==AttendanceStatus.ATTENDANCE) attendCount++;
        else if(status==AttendanceStatus.LATE) lateCount++;
        else if(status==AttendanceStatus.ABSENT) absentCount++;
        updateRiskLevel();
    }

    public void revertAttendanceStatus(AttendanceStatus status) {
        if(status==AttendanceStatus.ATTENDANCE) attendCount--;
        else if(status==AttendanceStatus.LATE) lateCount--;
        else if(status==AttendanceStatus.ABSENT) absentCount--;
        updateRiskLevel();
    }

    private void updateRiskLevel() {
        int effectiveAbsents = absentCount + (lateCount / 3);

        if (effectiveAbsents > 5) {
            riskLevel = AttendanceRiskLevel.EXPULSION;
            return;
        }
        if (effectiveAbsents >= 3) {
            riskLevel = AttendanceRiskLevel.COUNSEL;
            return;
        }
        if (effectiveAbsents >= 2) {
            riskLevel = AttendanceRiskLevel.WARNING;
            return;
        }
        riskLevel = AttendanceRiskLevel.NORMAL;
    }

    public int getAttendCount() {
        return attendCount;
    }

    public int getLateCount() {
        return lateCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public AttendanceRiskLevel getRiskLevel() {
        return riskLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crew crew = (Crew) o;
        return Objects.equals(name, crew.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
