package attendance.controller;

import attendance.model.Attendance;
import attendance.model.AttendanceBook;
import attendance.model.AttendanceRiskLevel;
import attendance.model.Crew;
import attendance.service.AttendanceFileLoaderService;
import attendance.view.InputView;
import attendance.view.OutputView;

import java.util.List;

public class AttendanceController {
    private final InputView inputView;
    private final OutputView outputView;
    private final AttendanceFileLoaderService attendanceFileLoaderService;
    private final AttendanceBook attendanceBook;


    public AttendanceController(InputView inputView, OutputView outputView,
                                AttendanceFileLoaderService attendanceFileLoaderService,AttendanceBook attendanceBook) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.attendanceFileLoaderService = attendanceFileLoaderService;
        this.attendanceBook = attendanceBook;
    }

    public void run(){
        attendanceFileLoaderService.load("src/main/resources/attendances.csv");

        while (true) {
            String option = inputView.readInputOption();
            if (option.equals("Q")) {
                break;
            }
            runOption(option);
        }
    }

    private void runOption(String option){
        if(option.equals("1")){
            runAttendanceRegister();
            return;
        }
        if(option.equals("2")){
            runAttendanceModify();
            return;
        }
        if(option.equals("3")){
            runAttendanceCheck();
            return;
        }
//        if(option.equals("4")){
//            runAttendanceRiskCheck();
//            return;
//        }
        throw new IllegalArgumentException("[ERROR] 잘못된 형식을 입력하였습니다.");
    }

    private void runAttendanceRegister(){
        String name = inputView.readInputName();
        String time = inputView.readInputTime();
        Attendance attendance = attendanceBook.registerAttendance(name,time);

        outputView.printAttendance(attendance);
    }

    private void runAttendanceModify(){
        String name = inputView.readModifyInputName();
        int day = Integer.parseInt(inputView.readModifyInputDay());
        String time = inputView.readModifyInputTime();

        List<Attendance> result = attendanceBook.modifyAttendance(name,day,time);
        Attendance before = result.get(0);
        Attendance after = result.get(1);

        outputView.printModifyResult(before, after);
    }

    private void runAttendanceCheck(){
        String name = inputView.readInputName();
        Crew crew = attendanceBook.getCrew(name);

        List<Attendance> records = attendanceBook.getAttendancesByCrew(crew);
        int attend = crew.getAttendCount();
        int late = crew.getLateCount();
        int absent = crew.getAbsentCount();
        AttendanceRiskLevel riskLevel = crew.getRiskLevel();

        outputView.printMonthlySummary(name, records, attend, late, absent, riskLevel);
    }

//    private void runAttendanceRiskCheck(){
//        List<RiskCheckDto> results = attendanceRiskCheckService.attendanceRiskCheck();
//        outputView.printCrewsRiskLevel(results);
//    }
}
