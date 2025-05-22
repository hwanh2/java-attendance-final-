package attendance.view;


import attendance.model.Attendance;
import attendance.model.AttendanceRiskLevel;
import attendance.model.Crew;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
        System.out.println();
    }

    public void printModifyResult(Attendance before, Attendance after) {
        String date = before.getDateTime().format(DATE_FORMATTER);
        String oldTime = before.getDateTime().toLocalTime().equals(LocalTime.MIDNIGHT)
                ? "--:--"
                : before.getDateTime().format(TIME_FORMATTER);
        String newTime = after.getDateTime().format(TIME_FORMATTER);
        String oldStatus = before.getStatus().getDisplayName();
        String newStatus = after.getStatus().getDisplayName();

        String result = String.format("%s %s (%s) -> %s (%s) 수정 완료!", date, oldTime, oldStatus, newTime, newStatus);
        System.out.println(result);
        System.out.println();
    }

    public void printMonthlySummary(String name,List<Attendance> records,int attend,int late,int absent,AttendanceRiskLevel riskLevel) {
        System.out.println();
        System.out.println("이번 달 " + name + "의 출석 기록입니다.");
        System.out.println();

        records.sort(Comparator.comparing(Attendance::getDateTime));

        for (Attendance a : records) {
            LocalDateTime dt = a.getDateTime();
            String date = dt.format(DATE_FORMATTER);
            String time = dt.toLocalTime().equals(LocalTime.MIDNIGHT) ? "--:--" : dt.format(TIME_FORMATTER);
            String status = a.getStatus().getDisplayName();

            String line = String.format("%s %s (%s)", date, time, status);
            System.out.println(line);
        }

        System.out.println();
        System.out.printf("출석: %d회%n", attend);
        System.out.printf("지각: %d회%n", late);
        System.out.printf("결석: %d회%n", absent);
        System.out.println();

        printRiskLevelMessage(riskLevel);
        System.out.println();
    }


    private void printRiskLevelMessage(AttendanceRiskLevel level) {
        switch (level) {
            case EXPULSION -> System.out.println("제적 대상자입니다.");
            case COUNSEL -> System.out.println("면담 대상자입니다.");
            case WARNING -> System.out.println("경고 대상자입니다.");
        }
    }

    public void printCrewsRiskLevel(List<Crew> results) {
        System.out.println("제적 위험자 조회 결과");

        for (Crew crew : results) {
            String name = crew.getName();
            int absent = crew.getAbsentCount();
            int late = crew.getLateCount();
            String level = crew.getRiskLevel().getDisplayName();

            System.out.printf("- %s: 결석 %d회, 지각 %d회 (%s)%n", name, absent, late, level);
        }
        System.out.println();
    }

}
