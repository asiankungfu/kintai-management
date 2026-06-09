package com.example.kintai.config;

import com.example.kintai.domain.ApprovalRequest;
import com.example.kintai.domain.AttendanceRecord;
import com.example.kintai.domain.Employee;
import com.example.kintai.domain.RequestType;
import com.example.kintai.domain.Role;
import com.example.kintai.repository.ApprovalRequestRepository;
import com.example.kintai.repository.AttendanceRecordRepository;
import com.example.kintai.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** 起動時にデモ用の社員・打刻・申請を投入する（DBが空のときのみ）。 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final EmployeeRepository employees;
    private final AttendanceRecordRepository records;
    private final ApprovalRequestRepository requests;

    public DataSeeder(EmployeeRepository employees,
                      AttendanceRecordRepository records,
                      ApprovalRequestRepository requests) {
        this.employees = employees;
        this.records = records;
        this.requests = requests;
    }

    @Override
    public void run(String... args) {
        if (employees.count() > 0) {
            return;
        }
        Employee manager = save("M001", "山田 太郎", "開発部", Role.MANAGER, null);
        Employee e1 = save("E001", "佐藤 花子", "開発部", Role.EMPLOYEE, manager.getId());
        Employee e2 = save("E002", "鈴木 一郎", "開発部", Role.EMPLOYEE, manager.getId());

        // 直近10営業日分の打刻（残業・深夜を含むパターン）
        LocalDate base = LocalDate.now().minusDays(13);
        int day = 0;
        for (int i = 0; i < 14 && day < 10; i++) {
            LocalDate d = base.plusDays(i);
            if (d.getDayOfWeek().getValue() >= 6) {
                continue; // 土日スキップ
            }
            day++;
            // 通常勤務 9:00-18:00 休憩60
            addRecord(e1, d, LocalTime.of(9, 0), d, LocalTime.of(18, 0), 60);
            // たまに残業 9:00-21:30
            if (day % 3 == 0) {
                addRecord(e2, d, LocalTime.of(9, 0), d, LocalTime.of(21, 30), 60);
            } else {
                addRecord(e2, d, LocalTime.of(9, 0), d, LocalTime.of(18, 0), 60);
            }
        }
        // 深夜にまたぐ夜勤の例（前日22:00→翌7:00）
        LocalDate nightDay = LocalDate.now().minusDays(2);
        addRecord(e1, nightDay, LocalTime.of(22, 0), nightDay.plusDays(1), LocalTime.of(7, 0), 60);

        // 申請（承認待ち）
        ApprovalRequest r = new ApprovalRequest();
        r.setApplicantId(e2.getId());
        r.setType(RequestType.OVERTIME);
        r.setTargetDate(LocalDate.now().minusDays(1));
        r.setMinutes(150);
        r.setReason("リリース対応のため");
        requests.save(r);

        ApprovalRequest r2 = new ApprovalRequest();
        r2.setApplicantId(e1.getId());
        r2.setType(RequestType.PAID_LEAVE);
        r2.setTargetDate(LocalDate.now().plusDays(7));
        r2.setMinutes(480);
        r2.setReason("私用のため");
        requests.save(r2);
    }

    private Employee save(String code, String name, String dept, Role role, Long managerId) {
        Employee e = new Employee();
        e.setCode(code);
        e.setName(name);
        e.setDepartment(dept);
        e.setRole(role);
        e.setManagerId(managerId);
        return employees.save(e);
    }

    private void addRecord(Employee e, LocalDate inDate, LocalTime in,
                           LocalDate outDate, LocalTime out, int breakMin) {
        AttendanceRecord r = new AttendanceRecord();
        r.setEmployeeId(e.getId());
        r.setWorkDate(inDate);
        r.setClockIn(LocalDateTime.of(inDate, in));
        r.setClockOut(LocalDateTime.of(outDate, out));
        r.setBreakMinutes(breakMin);
        records.save(r);
    }
}
