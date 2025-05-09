package attendance.service;

import attendance.model.AttendanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AttendanceStatusCalculatorServiceTest {

    private final LocalTime classStartTime = LocalTime.of(10, 0); // 수업 시작 시간

    @Test
    @DisplayName("정시 출석은 ATTENDANCE")
    void 출석은_ATTENDANCE() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 10, 4);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @Test
    @DisplayName("5분 초과 30분 이내면 LATE")
    void 지각은_LATE() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 10, 10);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.LATE);
    }

    @Test
    @DisplayName("30분 초과하면 ABSENT")
    void 결석은_ABSENT() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 10, 40);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    @DisplayName("정확히 5분이면 ATTENDANCE")
    void 정확히5분_ATTENDANCE() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 10, 5);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }

    @Test
    @DisplayName("정확히 30분이면 LATE")
    void 정확히30분_LATE() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 10, 30);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.LATE);
    }

    @Test
    @DisplayName("수업보다 이른 시간도 ATTENDANCE")
    void 수업보다_이르면_ATTENDANCE() {
        LocalDateTime attendTime = LocalDateTime.of(2025, 5, 1, 9, 50);

        AttendanceStatus result = AttendanceStatusCalculatorService.calculate(attendTime, classStartTime);

        assertThat(result).isEqualTo(AttendanceStatus.ATTENDANCE);
    }
}
