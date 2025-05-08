package attendance.view;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.AttendanceRiskLevel;
import attendance.model.Attendance;
import attendance.model.AttendanceStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OutputView {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM월 dd일 E요일 HH:mm", Locale.KOREAN);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM월 dd일 E요일", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");


    public void printAttendance(Attendance attendance) {
        LocalDateTime dateTime = attendance.getDateTime();
        String formattedDate = dateTime.format(FORMATTER);
        String statusText = attendance.getStatus().getDisplayName();

        System.out.println(formattedDate + " (" + statusText + ")");
    }

    public void printModifyResult(Attendance before, Attendance after) {
        String date = before.getDateTime().format(DATE_FORMATTER);
        String oldTime = before.getDateTime().format(TIME_FORMATTER);
        String newTime = after.getDateTime().format(TIME_FORMATTER);
        String oldStatus = before.getStatus().getDisplayName();
        String newStatus = after.getStatus().getDisplayName();

        String result = String.format("%s %s (%s) -> %s (%s) 수정 완료!", date, oldTime, oldStatus, newTime, newStatus);
        System.out.println(result);
    }

    public void printMonthlySummary(AttendanceCheckDto dto) {
        System.out.println();
        System.out.println("이번 달 " + dto.getName() + "의 출석 기록입니다.");
        System.out.println();

        List<Attendance> records = dto.getRecords();
        for (Attendance a : records) {
            LocalDateTime dt = a.getDateTime();
            String date = dt.format(DATE_FORMATTER);
            String time = (a.getStatus() == AttendanceStatus.ABSENT) ? "--:--" : dt.format(TIME_FORMATTER);
            String status = a.getStatus().getDisplayName();

            String line = String.format("%s %s (%s)", date, time, status);
            System.out.println(line);
        }

        System.out.println();
        System.out.println(String.format("출석: %d회", dto.getAttendCount()));
        System.out.println(String.format("지각: %d회", dto.getLateCount()));
        System.out.println(String.format("결석: %d회", dto.getAbsentCount()));
        System.out.println();

        printRiskLevelMessage(dto.getRiskLevel());
    }

    private void printRiskLevelMessage(AttendanceRiskLevel level) {
        switch (level) {
            case EXPULSION -> System.out.println("제적 대상자입니다.");
            case COUNSEL -> System.out.println("면담 대상자입니다.");
            case WARNING -> System.out.println("경고 대상자입니다.");
        }
    }

}
