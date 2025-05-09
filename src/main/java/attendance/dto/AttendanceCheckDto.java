package attendance.dto;

import attendance.model.Attendance;

import java.util.List;

public class AttendanceCheckDto {
    private final String name;
    private final List<Attendance> records;
    private final int attendCount;
    private final int lateCount;
    private final int absentCount;
    private final AttendanceRiskLevel riskLevel;

    public AttendanceCheckDto(String name, List<Attendance> records, int attendCount, int lateCount, int absentCount, AttendanceRiskLevel riskLevel) {
        this.name = name;
        this.records = records;
        this.attendCount = attendCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.riskLevel = riskLevel;
    }

    public String getName() {
        return name;
    }

    public List<Attendance> getRecords() {
        return records;
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
}
