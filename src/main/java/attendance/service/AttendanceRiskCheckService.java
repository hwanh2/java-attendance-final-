package attendance.service;

import attendance.dto.AttendanceRiskLevel;
import attendance.dto.RiskCheckDto;
import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendanceRiskCheckService {
    private final CrewRepository crewRepository;

    private static final int LATE_PER_ABSENT = 3;
    private static final int WARNING_THRESHOLD = 2;
    private static final int COUNSEL_THRESHOLD = 3;
    private static final int EXPULSION_THRESHOLD = 5;

    public AttendanceRiskCheckService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public List<RiskCheckDto> attendanceRiskCheck() {
        List<RiskCheckDto> results = new ArrayList<>();
        Month thisMonth = LocalDate.now().getMonth();

        for (Map.Entry<String, Crew> entry : crewRepository.getAllCrews().entrySet()) {
            RiskCheckDto dto = calculateRiskLevelForCrew(entry.getKey(), entry.getValue(), thisMonth);
            if (dto != null) {
                results.add(dto);
            }
        }

        return results;
    }

    private RiskCheckDto calculateRiskLevelForCrew(String name, Crew crew, Month thisMonth) {
        int late = 0;
        int absent = 0;

        for (Attendance a : crew.getRecords()) {
            if (a.getDateTime().getMonth() != thisMonth) continue;
            if (a.getStatus() == AttendanceStatus.LATE) late++;
            if (a.getStatus() == AttendanceStatus.ABSENT) absent++;
        }

        int effectiveAbsents = absent + (late / LATE_PER_ABSENT);
        AttendanceRiskLevel level = getLevel(effectiveAbsents);

        if (level == AttendanceRiskLevel.NORMAL) return null;
        return new RiskCheckDto(name, absent, late, level);
    }

    private AttendanceRiskLevel getLevel(int effectiveAbsents) {
        if (effectiveAbsents > EXPULSION_THRESHOLD) return AttendanceRiskLevel.EXPULSION;
        if (effectiveAbsents >= COUNSEL_THRESHOLD) return AttendanceRiskLevel.COUNSEL;
        if (effectiveAbsents >= WARNING_THRESHOLD) return AttendanceRiskLevel.WARNING;
        return AttendanceRiskLevel.NORMAL;
    }
}
