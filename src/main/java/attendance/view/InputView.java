package attendance.view;

import camp.nextstep.edu.missionutils.Console;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

public class InputView {
    public String readInputOption() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String koreanDay = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);

        System.out.println("오늘은 " + today.getMonthValue() + "월 " + today.getDayOfMonth() + "일 " + koreanDay + "입니다.");
        System.out.println("기능을 선택해 주세요.");
        System.out.println("1. 출석 확인");
        System.out.println("2. 출석 수정");
        System.out.println("3. 크루별 출석 기록 확인");
        System.out.println("4. 제적 위험자 확인");
        System.out.println("Q. 종료");

        return Console.readLine().strip();
    }

    public String readInputName(){
        System.out.println("닉네임을 입력해 주세요.");
        return Console.readLine().strip();
    }

    public String readInputTime() {
        System.out.println("등교 시간을 입력해 주세요.");
        return Console.readLine().strip();
    }

    public String readModifyInputName(){
        System.out.println("출석을 수정하려는 크루의 닉네임을 입력해 주세요.");
        return Console.readLine().strip();
    }

    public String readModifyInputDay(){
        System.out.println("수정하려는 날짜(일)를 입력해 주세요.");
        return Console.readLine().strip();
    }

    public String readModifyInputTime(){
        System.out.println("언제로 변경하겠습니까?");
        return Console.readLine().strip();
    }

}
