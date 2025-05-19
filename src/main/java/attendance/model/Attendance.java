package attendance.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Attendance {
    private static final int ALLOWED_MINUTES_FOR_ATTENDANCE = 5;
    private static final int ALLOWED_MINUTES_FOR_LATE = 30;

    private final LocalDateTime dateTime;
    private final AttendanceStatus status;

    private Attendance(LocalDateTime dateTime, AttendanceStatus status) {
        this.dateTime = dateTime;
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public static Attendance from(LocalDateTime dateTime, LocalTime classStartTime) {
        AttendanceStatus status = calculateStatus(dateTime, classStartTime);
        return new Attendance(dateTime, status);
    }

    private static AttendanceStatus calculateStatus(LocalDateTime attendTime, LocalTime classStartTime) {
        int attend = attendTime.getHour() * 60 + attendTime.getMinute();
        int start = classStartTime.getHour() * 60 + classStartTime.getMinute();
        int diff = attend - start;

        if (diff <= ALLOWED_MINUTES_FOR_ATTENDANCE) return AttendanceStatus.ATTENDANCE;
        if (diff <= ALLOWED_MINUTES_FOR_LATE) return AttendanceStatus.LATE;
        return AttendanceStatus.ABSENT;
    }
}
