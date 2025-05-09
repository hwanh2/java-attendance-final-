package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.*;
import java.util.List;

public class AttendanceModifyService {
    private final CrewRepository crewRepository;

    private static final LocalTime MONDAY_CLASS_START = LocalTime.of(13, 0);
    private static final LocalTime DEFAULT_CLASS_START = LocalTime.of(10, 0);

    public AttendanceModifyService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public List<Attendance> modify(String name, int dayOfMonth, String newTimeInput) {
        Crew crew = crewRepository.findByName(name);
        if (crew == null) {
            throw new IllegalArgumentException("[ERROR] 등록되지 않은 닉네임입니다.");
        }

        YearMonth currentMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDate targetDate = currentMonth.atDay(dayOfMonth);

        if (targetDate.isAfter(today)) {
            throw new IllegalArgumentException("[ERROR] 미래 날짜로는 출석을 수정할 수 없습니다.");
        }

        LocalTime newTime = LocalTime.parse(newTimeInput);
        LocalDateTime newDateTime = LocalDateTime.of(targetDate, newTime);
        LocalTime classStart = (targetDate.getDayOfWeek() == DayOfWeek.MONDAY)
                ? MONDAY_CLASS_START
                : DEFAULT_CLASS_START;
        AttendanceStatus newStatus = AttendanceStatusCalculatorService.calculate(newDateTime, classStart);
        Attendance newAttendance = Attendance.from(newDateTime, newStatus);

        List<Attendance> records = crew.getRecords();

        for (int i = 0; i < records.size(); i++) {
            Attendance record = records.get(i);
            if (record.getDateTime().toLocalDate().equals(targetDate)) {
                Attendance before = record;
                records.set(i, newAttendance); // 수정
                return List.of(before, newAttendance);
            }
        }

        // 출석이 없으면 새로 추가
        records.add(newAttendance);
        Attendance before = Attendance.from(newDateTime.withHour(0).withMinute(0), AttendanceStatus.ABSENT);
        return List.of(before, newAttendance); // before가 없음
    }
}
