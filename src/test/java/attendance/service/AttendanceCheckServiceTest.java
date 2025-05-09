package attendance.service;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.AttendanceRiskLevel;
import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.repository.CrewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceCheckServiceTest {

    private CrewRepository repository;
    private AttendanceCheckService service;

    @BeforeEach
    void setUp() {
        repository = new CrewRepository();
        service = new AttendanceCheckService(repository);
    }

    @Test
    void 출석_지각_결석_정상_카운트와_위험_판단() {
        String name = "이든";
        LocalDate thisMonthDate = LocalDate.now().withDayOfMonth(1);

        repository.addAttendance(name, Attendance.from(thisMonthDate.atTime(10, 0), AttendanceStatus.ATTENDANCE));
        repository.addAttendance(name, Attendance.from(thisMonthDate.plusDays(1).atTime(10, 10), AttendanceStatus.LATE));
        repository.addAttendance(name, Attendance.from(thisMonthDate.plusDays(2).atTime(10, 30), AttendanceStatus.LATE));
        repository.addAttendance(name, Attendance.from(thisMonthDate.plusDays(3).atTime(10, 50), AttendanceStatus.LATE));
        repository.addAttendance(name, Attendance.from(thisMonthDate.plusDays(4).atTime(0, 0), AttendanceStatus.ABSENT));

        AttendanceCheckDto result = service.AttendanceCheck(name);

        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getAttendCount()).isEqualTo(1);
        assertThat(result.getLateCount()).isEqualTo(3);
        assertThat(result.getAbsentCount()).isEqualTo(1);
        assertThat(result.getRiskLevel()).isEqualTo(AttendanceRiskLevel.WARNING); // 1 결석 + 3 지각 = 결석 2
    }
}
