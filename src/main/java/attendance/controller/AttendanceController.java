package attendance.controller;

import attendance.dto.AttendanceCheckDto;
import attendance.dto.AttendanceRiskLevel;
import attendance.dto.RiskCheckDto;
import attendance.model.Attendance;
import attendance.service.*;
import attendance.view.InputView;
import attendance.view.OutputView;

import java.util.List;

public class AttendanceController {
    private final InputView inputView;
    private final OutputView outputView;
    private final AttendanceCheckService attendanceCheckService;
    private final AttendanceFileLoaderService attendanceFileLoaderService;
    private final AttendanceModifyService attendanceModifyService;
    private final AttendanceRegisterService attendanceRegisterService;
    private final AttendanceRiskCheckService attendanceRiskCheckService;

    public AttendanceController(InputView inputView, OutputView outputView,
                                AttendanceCheckService attendanceCheckService,
                                AttendanceFileLoaderService attendanceFileLoaderService,
                                AttendanceModifyService attendanceModifyService,
                                AttendanceRegisterService attendanceRegisterService,
                                AttendanceRiskCheckService attendanceRiskCheckService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.attendanceCheckService = attendanceCheckService;
        this.attendanceFileLoaderService = attendanceFileLoaderService;
        this.attendanceModifyService = attendanceModifyService;
        this.attendanceRegisterService = attendanceRegisterService;
        this.attendanceRiskCheckService = attendanceRiskCheckService;
    }

    public void run(){
        attendanceFileLoaderService.load("src/main/resources/attendances.csv");

        while (true){
            try{
                String option = inputView.readInputOption();
                runOption(option);
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void runOption(String option){
        if(option.equals("1")){
            runAttendanceRegister();
        }
        if(option.equals("2")){
            runAttendanceModify();
        }
        if(option.equals("3")){
            runAttendanceCheck();
        }
        if(option.equals("4")){
            runAttendanceRiskCheck();
        }
    }

    private void runAttendanceRegister(){
        String name = inputView.readInputName();
        String time = inputView.readInputTime();
        Attendance attendance = attendanceRegisterService.register(name,time);

        outputView.printAttendance(attendance);
    }

    private void runAttendanceModify(){
        String name = inputView.readModifyInputName();
        int day = Integer.parseInt(inputView.readModifyInputDay());
        String time = inputView.readModifyInputTime();

        List<Attendance> result = attendanceModifyService.modify(name, day, time);
        Attendance before = result.get(0);
        Attendance after = result.get(1);

        outputView.printModifyResult(before, after);
    }

    private void runAttendanceCheck(){
        String name = inputView.readInputName();
        AttendanceCheckDto attendanceCheckDto = attendanceCheckService.AttendanceCheck(name);

        outputView.printMonthlySummary(attendanceCheckDto);
    }

    private void runAttendanceRiskCheck(){
        List<RiskCheckDto> results = attendanceRiskCheckService.attendanceRiskCheck();
        outputView.printCrewsRiskLevel(results);
    }
}
