package attendance.service;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.AttendanceRiskLevel;
import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Attendance> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        Month month = today.getMonth();

        Set<LocalDate> recordedDates = new HashSet<>();
        for (Attendance a : crew.getRecords()) {
            if (a.getDateTime().getMonth() == month && a.getDateTime().getYear() == year) {
                result.add(a);
                recordedDates.add(a.getDateTime().toLocalDate());
            }
        }

        // 공휴일 제외
        Set<LocalDate> holidays = Set.of(
                LocalDate.of(2025, 5, 5),
                LocalDate.of(2025, 5, 6)
        );

        for (int day = 1; day <= today.getDayOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            if (date.getDayOfWeek().getValue() >= 6) continue; // 토, 일
            if (holidays.contains(date)) continue;
            if (!recordedDates.contains(date)) {
                LocalDateTime absentTime = LocalDateTime.of(date, LocalTime.of(0, 0));
                result.add(Attendance.from(absentTime, AttendanceStatus.ABSENT));
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
