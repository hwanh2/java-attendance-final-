package attendance.model;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

public class AttendanceBook {
    private final Map<Crew, List<Attendance>> attendanceBook = new HashMap<>();

    private static final LocalTime MONDAY_CLASS_START = LocalTime.of(13, 0);
    private static final LocalTime OTHER_DAYS_CLASS_START = LocalTime.of(10, 0);
    private static final String DUPLICATE_ATTENDANCE_ERROR = "[ERROR] 이미 출석을 확인하였습니다. 필요한 경우 수정 기능을 이용해 주세요.";
    private static final String UNKNOWN_NAME_ERROR = "[ERROR] 등록되지 않은 닉네임입니다.";
    private static final String FUTURE_DATE_ERROR = "[ERROR] 미래 날짜로는 출석을 수정할 수 없습니다.";

    // 출석 등록
    public Attendance registerAttendance(String name, String timeInput) {
        LocalDate today = LocalDate.now();
        LocalTime inputTime = LocalTime.parse(timeInput);
        LocalDateTime dateTime = LocalDateTime.of(today, inputTime);
        DayOfWeek day = today.getDayOfWeek();

        validateWeekend(today,day);

        Crew crew = findCrewOrThrow(name);
        validateAttended(crew, today);

        LocalTime classStart = (day == DayOfWeek.MONDAY) ? MONDAY_CLASS_START : OTHER_DAYS_CLASS_START;

        Attendance attendance = Attendance.from(dateTime, classStart);

        attendanceBook.get(crew).add(attendance);

        return attendance;
    }

    private Crew findCrewOrThrow(String name) {
        for (Crew crew : attendanceBook.keySet()) {
            if (crew.getName().equals(name)) {
                return crew;
            }
        }
        throw new IllegalArgumentException(UNKNOWN_NAME_ERROR );
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

    private void validateWeekend(LocalDate today,DayOfWeek day){
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            String dayName = day.getDisplayName(TextStyle.FULL, Locale.KOREAN);
            throw new IllegalArgumentException(String.format("[ERROR] %d월 %d일 %s은 등교일이 아닙니다.",
                    today.getMonthValue(),
                    today.getDayOfMonth(),
                    dayName
            ));
        }
    }

    public List<Attendance> modifyAttendance(String name, int dayOfMonth, String newTimeInput) {
        Crew crew = findCrewOrThrow(name);

        LocalDate targetDate = validateAndGetTargetDate(dayOfMonth);

        LocalTime newTime = LocalTime.parse(newTimeInput);
        LocalDateTime newDateTime = LocalDateTime.of(targetDate, newTime);

        LocalTime classStart = (targetDate.getDayOfWeek() == DayOfWeek.MONDAY) ? MONDAY_CLASS_START : OTHER_DAYS_CLASS_START;

        Attendance newAttendance = Attendance.from(newDateTime, classStart);

        List<Attendance> records = attendanceBook.get(crew);

        Attendance before = findAndReplace(records, targetDate, newAttendance);
        if (before != null) {
            return List.of(before, newAttendance);
        }

        // 기존 출석 기록이 없으면 새로 추가
        records.add(newAttendance);
        before = Attendance.from(newDateTime.withHour(0).withMinute(0), classStart); // 가상의 결석 상태
        return List.of(before, newAttendance);
    }

    public void addAttendance(String name, Attendance attendance) {
        Crew crew = Crew.from(name);
        if (!attendanceBook.containsKey(crew)) {
            attendanceBook.put(crew, new ArrayList<>());
        }
        attendanceBook.get(crew).add(attendance);
    }

    // 크루별 출석 목록 조회
    public List<Attendance> getAttendancesByCrew(Crew crew) {
        return attendanceBook.get(crew);
    }

    // 전체 출석부 조회
    public Map<Crew, List<Attendance>> getAllAttendances() {
        return attendanceBook;
    }

    private LocalDate validateAndGetTargetDate(int dayOfMonth) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate today = LocalDate.now();
        LocalDate targetDate = currentMonth.atDay(dayOfMonth);

        if (targetDate.isAfter(today)) {
            throw new IllegalArgumentException(FUTURE_DATE_ERROR);
        }

        return targetDate;
    }

    private Attendance findAndReplace(List<Attendance> records, LocalDate targetDate, Attendance newAttendance) {
        for (int i = 0; i < records.size(); i++) {
            Attendance record = records.get(i);
            if (record.getDateTime().toLocalDate().equals(targetDate)) {
                Attendance before = record;
                records.set(i, newAttendance);
                return before;
            }
        }
        return null;
    }
}
