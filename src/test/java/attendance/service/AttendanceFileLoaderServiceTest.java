package attendance.service;

import attendance.model.Attendance;
import attendance.model.Crew;
import attendance.repository.CrewRepository;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceFileLoaderServiceTest {

    @Test
    void 출석_파일_불러오기_및_날짜파싱_정확성_테스트() throws Exception {
        // 임시 CSV 파일 생성
        Path tempFile = Files.createTempFile("attendance", ".csv");
        FileWriter writer = new FileWriter(tempFile.toFile());
        writer.write("nickname,datetime\n");
        writer.write("쿠키,2025-05-01 10:02\n");
        writer.close();

        // 준비
        CrewRepository repo = new CrewRepository();
        AttendanceFileLoaderService loader = new AttendanceFileLoaderService(repo);

        // 실행
        loader.load(tempFile.toString());

        // 검증
        Crew crew = repo.findByName("쿠키");
        assertThat(crew).isNotNull();
        assertThat(crew.getRecords()).hasSize(1);

        Attendance attendance = crew.getRecords().get(0);
        LocalDateTime dateTime = attendance.getDateTime();
        assertThat(dateTime.getYear()).isEqualTo(2025);
        assertThat(dateTime.getMonthValue()).isEqualTo(5);
        assertThat(dateTime.getDayOfMonth()).isEqualTo(1);
        assertThat(dateTime.getHour()).isEqualTo(10);
        assertThat(dateTime.getMinute()).isEqualTo(2);

        // 정리
        Files.deleteIfExists(tempFile);
    }
}
