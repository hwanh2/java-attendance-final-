package attendance.service;

import attendance.model.Attendance;
import attendance.model.AttendanceStatus;
import attendance.repository.CrewRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.Set;


public class AttendanceFileLoaderService {
    private final CrewRepository crewRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Set<LocalDate> HOLIDAYS = Set.of(
            LocalDate.of(2025, 5, 5),
            LocalDate.of(2025, 5, 6)
    );

    public AttendanceFileLoaderService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public void load(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) continue;

                String name = parts[0].trim();
                LocalDateTime dateTime = LocalDateTime.parse(parts[1].trim(), FORMATTER);

                DayOfWeek day = dateTime.getDayOfWeek();
                LocalDate date = dateTime.toLocalDate();

                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) continue;
                if (HOLIDAYS.contains(date)) continue; // 공휴일 제외

                LocalTime classStartTime = getClassStartTime(day);
                AttendanceStatus status = AttendanceStatusCalculatorService.calculate(dateTime, classStartTime);
                Attendance attendance = Attendance.from(dateTime, status);

                crewRepository.addAttendance(name, attendance);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] CSV 파일 로딩 실패: " + e.getMessage());
        }
    }

    private LocalTime getClassStartTime(DayOfWeek day) {
        return (day == DayOfWeek.MONDAY) ? LocalTime.of(13, 0) : LocalTime.of(10, 0);
    }
}
