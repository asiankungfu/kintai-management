package com.example.kintai.web;

import com.example.kintai.domain.Employee;
import com.example.kintai.repository.EmployeeRepository;
import com.example.kintai.service.MonthlySummaryService;
import com.example.kintai.service.WorkTimeResult;
import com.example.kintai.service.dto.MonthlySummary;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 月次勤怠集計を全社員分まとめて CSV でダウンロードする。
 */
@RestController
public class CsvExportController {

    private final EmployeeRepository employeeRepository;
    private final MonthlySummaryService summaryService;

    public CsvExportController(EmployeeRepository employeeRepository,
                              MonthlySummaryService summaryService) {
        this.employeeRepository = employeeRepository;
        this.summaryService = summaryService;
    }

    /** 例: GET /export/monthly.csv?year=2026&month=6 */
    @GetMapping("/export/monthly.csv")
    public ResponseEntity<byte[]> export(@RequestParam int year, @RequestParam int month) {
        StringBuilder sb = new StringBuilder();
        sb.append("社員コード,氏名,部署,出勤日数,労働時間,残業時間,深夜時間\n");
        for (Employee e : employeeRepository.findAll()) {
            MonthlySummary s = summaryService.summarize(e.getId(), year, month);
            sb.append(e.getCode()).append(',')
              .append(e.getName()).append(',')
              .append(e.getDepartment() == null ? "" : e.getDepartment()).append(',')
              .append(s.daysPresent()).append(',')
              .append(WorkTimeResult.toHhmm(s.workedMinutes())).append(',')
              .append(WorkTimeResult.toHhmm(s.overtimeMinutes())).append(',')
              .append(WorkTimeResult.toHhmm(s.lateNightMinutes())).append('\n');
        }
        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        String filename = String.format("kintai_%04d%02d.csv", year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }
}
