package com.example.kintai.service.dto;

import com.example.kintai.service.WorkTimeResult;

/**
 * 月次の勤怠集計（社員1名分）。
 *
 * @param year           対象年
 * @param month          対象月
 * @param daysPresent    出勤日数
 * @param workedMinutes  総労働時間（分）
 * @param overtimeMinutes 総残業時間（分）
 * @param lateNightMinutes 総深夜時間（分）
 */
public record MonthlySummary(
        int year, int month, int daysPresent,
        long workedMinutes, long overtimeMinutes, long lateNightMinutes) {

    public String workedLabel() {
        return WorkTimeResult.toHhmm(workedMinutes);
    }

    public String overtimeLabel() {
        return WorkTimeResult.toHhmm(overtimeMinutes);
    }

    public String lateNightLabel() {
        return WorkTimeResult.toHhmm(lateNightMinutes);
    }
}
