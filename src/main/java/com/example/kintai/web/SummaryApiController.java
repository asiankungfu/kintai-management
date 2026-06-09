package com.example.kintai.web;

import com.example.kintai.service.MonthlySummaryService;
import com.example.kintai.service.dto.MonthlySummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 月次集計を JSON で返す REST API。 */
@RestController
public class SummaryApiController {

    private final MonthlySummaryService summaryService;

    public SummaryApiController(MonthlySummaryService summaryService) {
        this.summaryService = summaryService;
    }

    /** 例: GET /api/employees/1/summary?year=2026&month=6 */
    @GetMapping("/api/employees/{empId}/summary")
    public MonthlySummary summary(@PathVariable Long empId,
                                  @RequestParam int year,
                                  @RequestParam int month) {
        return summaryService.summarize(empId, year, month);
    }
}
