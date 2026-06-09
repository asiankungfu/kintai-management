package com.example.kintai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1日1件の勤怠記録（打刻）。
 *
 * <p>労働時間・残業・深夜は出退勤と休憩から {@code WorkTimeCalculator} で算出するため、
 * 本エンティティは生データ（打刻・休憩）のみを保持する。</p>
 */
@Entity
@Table(name = "attendance_record",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "work_date"}))
@Getter
@Setter
@NoArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    /** 出勤打刻。 */
    private LocalDateTime clockIn;

    /** 退勤打刻（翌日にまたがる場合は翌日日時）。 */
    private LocalDateTime clockOut;

    /** 休憩（分）。 */
    private int breakMinutes;

    public boolean isClosed() {
        return clockIn != null && clockOut != null;
    }
}
