package attendance.view;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.RiskCheckDto;
import attendance.model.Attendance;

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

    public void printMonthlySummary(AttendanceCheckDto dto) {
        System.out.println();
        System.out.println("이번 달 " + dto.getName() + "의 출석 기록입니다.");
        System.out.println();

        List<Attendance> records = dto.getRecords();
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
        System.out.println(String.format("출석: %d회", dto.getAttendCount()));
        System.out.println(String.format("지각: %d회", dto.getLateCount()));
        System.out.println(String.format("결석: %d회", dto.getAbsentCount()));
        System.out.println();

        printRiskLevelMessage(dto.getRiskLevel());
        System.out.println();
    }

    private void printRiskLevelMessage(AttendanceRiskLevel level) {
        switch (level) {
            case EXPULSION -> System.out.println("제적 대상자입니다.");
            case COUNSEL -> System.out.println("면담 대상자입니다.");
            case WARNING -> System.out.println("경고 대상자입니다.");
        }
    }

    public void printCrewsRiskLevel(List<RiskCheckDto> results) {
        System.out.println("제적 위험자 조회 결과");

        results.sort(new Comparator<RiskCheckDto>() {
            @Override
            public int compare(RiskCheckDto a, RiskCheckDto b) {
                // 1. 위험 레벨 우선순위: EXPULSION > COUNSEL > WARNING
                int levelCompare = b.getRiskLevel().ordinal() - a.getRiskLevel().ordinal();
                if (levelCompare != 0) return levelCompare;

                // 2. 결석 + (지각 / 3) 내림차순
                int aScore = a.getAbsentCount() + a.getLateCount() / 3;
                int bScore = b.getAbsentCount() + b.getLateCount() / 3;
                if (aScore != bScore) return bScore - aScore;

                // 3. 이름 오름차순
                return a.getName().compareTo(b.getName());
            }
        });

        for (RiskCheckDto dto : results) {
            String line = String.format("- %s: 결석 %d회, 지각 %d회 (%s)",
                    dto.getName(), dto.getAbsentCount(), dto.getLateCount(), dto.getRiskLevel().getDisplayName());
            System.out.println(line);
        }
        System.out.println();
    }

}
