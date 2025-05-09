package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceRegisterService {
    private final CrewRepository crewRepository;

    private static final LocalTime MONDAY_CLASS_START = LocalTime.of(13, 0);
    private static final LocalTime OTHER_DAYS_CLASS_START = LocalTime.of(10, 0);

    private static final String DUPLICATE_ATTENDANCE_ERROR = "[ERROR] 이미 출석을 확인하였습니다. 필요한 경우 수정 기능을 이용해 주세요.";

    public AttendanceRegisterService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public Attendance register(String name, String timeInput) {
        LocalDate today = LocalDate.now();
        LocalTime inputTime = LocalTime.parse(timeInput);
        LocalDateTime dateTime = LocalDateTime.of(today, inputTime);

        DayOfWeek day = today.getDayOfWeek();

        validateNotAlreadyAttended(name, today);

        LocalTime classStart = (day == DayOfWeek.MONDAY) ? MONDAY_CLASS_START : OTHER_DAYS_CLASS_START;
        AttendanceStatus status = AttendanceStatusCalculatorService.calculate(dateTime, classStart);
        Attendance attendance = Attendance.from(dateTime, status);

        crewRepository.addAttendance(name, attendance);

        return attendance;
    }

    private void validateNotAlreadyAttended(String name, LocalDate today) {
        Crew crew = crewRepository.findByName(name);
        if (crew == null) return;

        for (Attendance a : crew.getRecords()) {
            if (a.getDateTime().toLocalDate().equals(today)) {
                throw new IllegalArgumentException(DUPLICATE_ATTENDANCE_ERROR);
            }
        }
    }
}
