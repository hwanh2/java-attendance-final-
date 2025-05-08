package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.*;

public class AttendanceModifyService {
    private final CrewRepository crewRepository;

    public AttendanceModifyService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public void modify(String name, int dayOfMonth, String newTimeInput) {
        Crew crew = crewRepository.findByName(name);
        if (crew == null) {
            throw new IllegalArgumentException("[ERROR] 등록되지 않은 닉네임입니다.");
        }

        YearMonth currentMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDate targetDate = currentMonth.atDay(dayOfMonth);

        // 미래 날짜 예외 처리
        if (targetDate.isAfter(today)) {
            throw new IllegalArgumentException("[ERROR] 미래 날짜로는 출석을 수정할 수 없습니다.");
        }

        LocalTime newTime = LocalTime.parse(newTimeInput);
        LocalDateTime newDateTime = LocalDateTime.of(targetDate, newTime);

        for (Attendance record : crew.getRecords()) {
            if (record.getDateTime().toLocalDate().equals(targetDate)) {
                LocalTime classStart = (targetDate.getDayOfWeek() == DayOfWeek.MONDAY) ? LocalTime.of(13, 0) : LocalTime.of(10, 0);
                AttendanceStatus status = AttendanceStatusCalculatorService.calculate(newDateTime, classStart);
                crew.getRecords().remove(record);
                crew.getRecords().add(Attendance.from(newDateTime, status));
            }
        }
    }
}
