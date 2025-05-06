package attendance.service;

import attendance.model.AttendanceStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceStatusCalculatorService {
    public static AttendanceStatus calculate(LocalDateTime attendTime, LocalTime classStartTime) {
        int attend = attendTime.getHour() * 60 + attendTime.getMinute();
        int start = classStartTime.getHour() * 60 + classStartTime.getMinute();
        int diff = attend - start;

        if (diff <= 5) return AttendanceStatus.ATTENDANCE;
        if (diff <= 30) return AttendanceStatus.LATE;
        return AttendanceStatus.ABSENT;
    }

}
