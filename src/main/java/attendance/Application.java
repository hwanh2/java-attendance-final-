package attendance;

import attendance.controller.AttendanceController;
import attendance.repository.CrewRepository;
import attendance.service.*;
import attendance.view.InputView;
import attendance.view.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        CrewRepository crewRepository = new CrewRepository();

        AttendanceFileLoaderService fileLoaderService = new AttendanceFileLoaderService(crewRepository);
        AttendanceRegisterService registerService = new AttendanceRegisterService(crewRepository);
        AttendanceModifyService modifyService = new AttendanceModifyService(crewRepository);
        AttendanceCheckService checkService = new AttendanceCheckService(crewRepository);
        AttendanceStatusCalculatorService calculatorService = new AttendanceStatusCalculatorService();

        AttendanceController controller = new AttendanceController(
                inputView,
                outputView,
                checkService,
                fileLoaderService,
                modifyService,
                registerService,
                calculatorService
        );

        controller.run();
    }
}
