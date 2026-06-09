package com.example.kintai.service;

import com.example.kintai.domain.AttendanceRecord;
import com.example.kintai.repository.AttendanceRecordRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 打刻と勤怠記録の管理を担うサービス。
 */
@Service
public class AttendanceService {

    private final AttendanceRecordRepository repository;

    public AttendanceService(AttendanceRecordRepository repository) {
        this.repository = repository;
    }

    /** 出勤打刻。当日のレコードが無ければ作成し、出勤時刻を記録する。 */
    @Transactional
    public AttendanceRecord clockIn(Long employeeId, LocalDateTime at) {
        LocalDate date = at.toLocalDate();
        AttendanceRecord rec = repository.findByEmployeeIdAndWorkDate(employeeId, date)
                .orElseGet(() -> {
                    AttendanceRecord r = new AttendanceRecord();
                    r.setEmployeeId(employeeId);
                    r.setWorkDate(date);
                    return r;
                });
        if (rec.getClockIn() != null) {
            throw new IllegalStateException("既に出勤打刻済みです");
        }
        rec.setClockIn(at);
        return repository.save(rec);
    }

    /** 退勤打刻。出勤打刻が前提。休憩分も記録する。 */
    @Transactional
    public AttendanceRecord clockOut(Long employeeId, LocalDateTime at, int breakMinutes) {
        LocalDate date = at.toLocalDate();
        // 夜勤で日付がまたぐ場合は当日→前日も探す
        AttendanceRecord rec = repository.findByEmployeeIdAndWorkDate(employeeId, date)
                .or(() -> repository.findByEmployeeIdAndWorkDate(employeeId, date.minusDays(1)))
                .orElseThrow(() -> new IllegalStateException("出勤打刻がありません"));
        if (rec.getClockIn() == null) {
            throw new IllegalStateException("出勤打刻がありません");
        }
        rec.setClockOut(at);
        rec.setBreakMinutes(breakMinutes);
        return repository.save(rec);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> recentRecords(Long employeeId) {
        return repository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> recordsInMonth(Long employeeId, int year, int month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.plusMonths(1).minusDays(1);
        return repository.findByEmployeeIdAndWorkDateBetweenOrderByWorkDateAsc(employeeId, from, to);
    }
}
