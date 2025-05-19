package attendance.model;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.service.AttendanceStatusCalculatorService;

import java.time.*;
import java.util.*;

public class AttendanceBook {
    private final Map<Crew, List<Attendance>> attendanceBook = new HashMap<>();

    private static final LocalTime MONDAY_CLASS_START = LocalTime.of(13, 0);
    private static final LocalTime OTHER_DAYS_CLASS_START = LocalTime.of(10, 0);
    private static final String DUPLICATE_ATTENDANCE_ERROR = "[ERROR] 이미 출석을 확인하였습니다. 필요한 경우 수정 기능을 이용해 주세요.";
    private static final String UNKNOWN_NAME_ERROR = "[ERROR] 등록되지 않은 닉네임입니다.";

    // 출석 등록
    public Attendance registerAttendance(String name, String timeInput) {
        LocalDate today = LocalDate.now();
        LocalTime inputTime = LocalTime.parse(timeInput);
        LocalDateTime dateTime = LocalDateTime.of(today, inputTime);
        DayOfWeek day = today.getDayOfWeek();

        Crew crew = Crew.from(name);
        validateAttended(crew, today);

        LocalTime classStart = (day == DayOfWeek.MONDAY) ? MONDAY_CLASS_START : OTHER_DAYS_CLASS_START;
        AttendanceStatus status = AttendanceStatusCalculatorService.calculate(dateTime, classStart);
        Attendance attendance = Attendance.from(dateTime, status);

        attendanceBook.get(crew).add(attendance);

        return attendance;
    }

    // 출석 중복 방지
    private void validateAttended(Crew crew, LocalDate today) {
        if (!attendanceBook.containsKey(crew)) {
            throw new IllegalArgumentException(UNKNOWN_NAME_ERROR);
        }
        List<Attendance> records = attendanceBook.get(crew);
        if (records == null) return;

        for (Attendance a : records) {
            if (a.getDateTime().toLocalDate().equals(today)) {
                throw new IllegalArgumentException(DUPLICATE_ATTENDANCE_ERROR);
            }
        }
    }

    // 크루별 출석 목록 조회
    public List<Attendance> getAttendancesByCrew(Crew crew) {
        return attendanceBook.get(crew);
    }

    // 전체 출석부 조회
    public Map<Crew, List<Attendance>> getAllAttendances() {
        return attendanceBook;
    }
}
