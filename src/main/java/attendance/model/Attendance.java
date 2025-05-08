package attendance.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Attendance {
    private LocalDateTime dateTime;
    private AttendanceStatus status;

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

    public static Attendance from(LocalDateTime dateTime, AttendanceStatus status){
        return new Attendance(dateTime,status);
    }



}
