package com.example.kintai.repository;

import com.example.kintai.domain.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<AttendanceRecord> findByEmployeeIdAndWorkDateBetweenOrderByWorkDateAsc(
            Long employeeId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);
}
