package com.example.kintai.service;

import com.example.kintai.domain.AttendanceRecord;
import com.example.kintai.service.dto.MonthlySummary;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 月次集計を行うサービス。打刻記録を {@link WorkTimeCalculator} で集計する。
 */
@Service
public class MonthlySummaryService {

    private final AttendanceService attendanceService;
    private final WorkTimeCalculator calculator;

    public MonthlySummaryService(AttendanceService attendanceService,
                                 WorkTimeCalculator calculator) {
        this.attendanceService = attendanceService;
        this.calculator = calculator;
    }

    @Transactional(readOnly = true)
    public MonthlySummary summarize(Long employeeId, int year, int month) {
        List<AttendanceRecord> records = attendanceService.recordsInMonth(employeeId, year, month);
        long worked = 0, overtime = 0, lateNight = 0;
        int days = 0;
        for (AttendanceRecord r : records) {
            if (!r.isClosed()) {
                continue;
            }
            WorkTimeResult res = calculator.calculate(r.getClockIn(), r.getClockOut(), r.getBreakMinutes());
            worked += res.workedMinutes();
            overtime += res.overtimeMinutes();
            lateNight += res.lateNightMinutes();
            days++;
        }
        return new MonthlySummary(year, month, days, worked, overtime, lateNight);
    }
}
