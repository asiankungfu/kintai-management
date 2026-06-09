package com.example.kintai.web;

import com.example.kintai.domain.AttendanceRecord;
import com.example.kintai.service.WorkTimeResult;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 画面表示用：勤怠記録＋計算結果。 */
public record AttendanceRow(
        LocalDate date, LocalDateTime clockIn, LocalDateTime clockOut, int breakMinutes,
        String workedLabel, String overtimeLabel, String lateNightLabel, boolean closed) {

    public static AttendanceRow of(AttendanceRecord r, WorkTimeResult res) {
        return new AttendanceRow(
                r.getWorkDate(), r.getClockIn(), r.getClockOut(), r.getBreakMinutes(),
                res.workedLabel(), res.overtimeLabel(), res.lateNightLabel(), r.isClosed());
    }
}
