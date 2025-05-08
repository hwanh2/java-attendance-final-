package attendance.service;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.AttendanceRiskLevel;
import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class AttendanceCheckService {
    private final CrewRepository crewRepository;

    private static final int LATE_PER_ABSENT = 3;
    private static final int WARNING_THRESHOLD = 2;
    private static final int COUNSEL_THRESHOLD = 3;
    private static final int EXPULSION_THRESHOLD = 5;

    public AttendanceCheckService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public AttendanceCheckDto AttendanceCheck(String name){
        Crew crew = crewRepository.findByName(name);
        if (crew == null) {
            throw new IllegalArgumentException("[ERROR] 등록되지 않은 닉네임입니다.");
        }

        List<Attendance> crewRecords = findRecords(crew);

        int attend = 0;
        int late = 0;
        int absent = 0;

        int size = crewRecords.size();
        for (int i = 0; i < size; i++) {
            AttendanceStatus status = crewRecords.get(i).getStatus();
            if (status == AttendanceStatus.ATTENDANCE) attend++;
            else if (status == AttendanceStatus.LATE) late++;
            else if (status == AttendanceStatus.ABSENT) absent++;
        }

        AttendanceRiskLevel level = determineRiskLevel(late, absent);

        return new AttendanceCheckDto(name, crewRecords, attend, late, absent, level);
    }

    private List<Attendance> findRecords(Crew crew) {
        Month thisMonth = LocalDate.now().getMonth();
        List<Attendance> result = new ArrayList<>();

        for (Attendance a : crew.getRecords()) {
            if (a.getDateTime().getMonth() == thisMonth) {
                result.add(a);
            }
        }

        return result;
    }

    private AttendanceRiskLevel determineRiskLevel(int late, int absent) {
        int effectiveAbsents = absent + (late / LATE_PER_ABSENT);

        if (effectiveAbsents > EXPULSION_THRESHOLD) return AttendanceRiskLevel.EXPULSION;
        if (effectiveAbsents >= COUNSEL_THRESHOLD) return AttendanceRiskLevel.COUNSEL;
        if (effectiveAbsents >= WARNING_THRESHOLD) return AttendanceRiskLevel.WARNING;
        return AttendanceRiskLevel.NORMAL;
    }
}
