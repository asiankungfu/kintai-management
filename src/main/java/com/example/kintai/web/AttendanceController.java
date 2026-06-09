package com.example.kintai.web;

import com.example.kintai.domain.AttendanceRecord;
import com.example.kintai.domain.Employee;
import com.example.kintai.repository.EmployeeRepository;
import com.example.kintai.service.AttendanceService;
import com.example.kintai.service.ResourceNotFoundException;
import com.example.kintai.service.WorkTimeCalculator;
import com.example.kintai.service.WorkTimeResult;
import com.example.kintai.service.dto.MonthlySummary;
import com.example.kintai.service.MonthlySummaryService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 打刻・勤怠表示。 */
@Controller
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final MonthlySummaryService summaryService;
    private final WorkTimeCalculator calculator;
    private final EmployeeRepository employeeRepository;

    public AttendanceController(AttendanceService attendanceService,
                                MonthlySummaryService summaryService,
                                WorkTimeCalculator calculator,
                                EmployeeRepository employeeRepository) {
        this.attendanceService = attendanceService;
        this.summaryService = summaryService;
        this.calculator = calculator;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees/{empId}/attendance")
    public String attendance(@PathVariable Long empId, Model model) {
        Employee emp = employee(empId);
        List<AttendanceRow> rows = new ArrayList<>();
        for (AttendanceRecord r : attendanceService.recentRecords(empId)) {
            WorkTimeResult res = calculator.calculate(r.getClockIn(), r.getClockOut(), r.getBreakMinutes());
            rows.add(AttendanceRow.of(r, res));
        }
        LocalDate today = LocalDate.now();
        MonthlySummary summary = summaryService.summarize(empId, today.getYear(), today.getMonthValue());

        model.addAttribute("employee", emp);
        model.addAttribute("rows", rows);
        model.addAttribute("summary", summary);
        return "attendance";
    }

    @PostMapping("/employees/{empId}/clock-in")
    public String clockIn(@PathVariable Long empId) {
        attendanceService.clockIn(empId, LocalDateTime.now());
        return "redirect:/employees/" + empId + "/attendance";
    }

    @PostMapping("/employees/{empId}/clock-out")
    public String clockOut(@PathVariable Long empId,
                           @RequestParam(defaultValue = "60") int breakMinutes) {
        attendanceService.clockOut(empId, LocalDateTime.now(), breakMinutes);
        return "redirect:/employees/" + empId + "/attendance";
    }

    private Employee employee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません: id=" + id));
    }
}
