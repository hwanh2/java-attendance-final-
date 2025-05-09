package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.model.Crew;
import attendance.repository.CrewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AttendanceRegisterServiceTest {
    private CrewRepository repository;
    private AttendanceRegisterService service;

    @BeforeEach
    void setUp() {
        repository = new CrewRepository();
        service = new AttendanceRegisterService(repository);
    }

    @Test
    void 출석_날짜_시간_요일_상태_확인() {
        // 입력
        String name = "이든";
        String timeInput = "10:06"; // 6분 늦음 → 지각

        service.register(name, timeInput);

        // 출석 정보 꺼내기
        Crew crew = repository.findByName(name);
        Attendance attendance = crew.getRecords().get(0);
        LocalDateTime dateTime = attendance.getDateTime();

        // 날짜 정보
        assertThat(dateTime.getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(dateTime.getMonthValue()).isEqualTo(LocalDate.now().getMonthValue());
        assertThat(dateTime.getDayOfMonth()).isEqualTo(LocalDate.now().getDayOfMonth());

        // 요일
        assertThat(dateTime.getDayOfWeek()).isEqualTo(LocalDate.now().getDayOfWeek());

        // 시간
        assertThat(dateTime.getHour()).isEqualTo(10);
        assertThat(dateTime.getMinute()).isEqualTo(6);

        // 상태
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
    }

}
