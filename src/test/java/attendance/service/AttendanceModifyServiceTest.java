package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AttendanceModifyServiceTest {
    private CrewRepository repository;
    private AttendanceModifyService modifyService;

    @BeforeEach
    void setUp() {
        repository = new CrewRepository();
        modifyService = new AttendanceModifyService(repository);
    }

    @Test
    void 출석_수정_확인() {
        // given
        String name = "쿠키";
        int day = LocalDate.now().getDayOfMonth();
        LocalDateTime origin = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5));
        Attendance original = Attendance.from(origin, AttendanceStatus.ATTENDANCE);
        repository.addAttendance(name, original);

        // when
        modifyService.modify(name, day, "10:31"); // 31분 → 결석

        // then
        Crew crew = repository.findByName(name);
        Attendance modified = crew.getRecords().get(0);

        assertThat(modified.getDateTime().toLocalTime()).isEqualTo(LocalTime.of(10, 31));
        assertThat(modified.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    void 미래_날짜_수정은_예외() {
        String name = "이든";
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(1);

        // 오늘 날짜 출석만 추가
        Attendance attendance = Attendance.from(today.atTime(10, 0), AttendanceStatus.ATTENDANCE);
        repository.addAttendance(name, attendance);

        assertThatThrownBy(() -> modifyService.modify(name, futureDate.getDayOfMonth(), "10:00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("미래 날짜");
    }


}
