package attendance.service;

import attendance.model.AttendanceStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceStatusCalculatorService {
    private static final int ALLOWED_MINUTES_FOR_ATTENDANCE = 5;
    private static final int ALLOWED_MINUTES_FOR_LATE = 30;

    public static AttendanceStatus calculate(LocalDateTime attendTime, LocalTime classStartTime) {
        int attend = attendTime.getHour() * 60 + attendTime.getMinute();
        int start = classStartTime.getHour() * 60 + classStartTime.getMinute();
        int diff = attend - start;

        if (diff <= ALLOWED_MINUTES_FOR_ATTENDANCE) return AttendanceStatus.ATTENDANCE;
        if (diff <= ALLOWED_MINUTES_FOR_LATE) return AttendanceStatus.LATE;
        return AttendanceStatus.ABSENT;
    }

}
