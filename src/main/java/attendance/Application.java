package attendance;

import attendance.controller.AttendanceController;
import attendance.model.AttendanceBook;
import attendance.service.*;
import attendance.view.InputView;
import attendance.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        AttendanceBook attendanceBook = new AttendanceBook();
        AttendanceFileLoaderService fileLoaderService = new AttendanceFileLoaderService(attendanceBook);

        AttendanceController controller = new AttendanceController(
                inputView,
                outputView,
                fileLoaderService,
                attendanceBook
        );

        controller.run();
    }
}
