package com.example.kintai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WorkTimeCalculatorTest {

    private final WorkTimeCalculator calc = new WorkTimeCalculator(480); // 所定8時間

    @Test
    @DisplayName("通常勤務 9:00-18:00 休憩60分 → 労働8h・残業0・深夜0")
    void normalDay() {
        WorkTimeResult r = calc.calculate(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 18, 0), 60);
        assertThat(r.workedMinutes()).isEqualTo(480);
        assertThat(r.overtimeMinutes()).isZero();
        assertThat(r.lateNightMinutes()).isZero();
    }

    @Test
    @DisplayName("残業 9:00-21:30 休憩60分 → 労働11.5h・残業3.5h・深夜0")
    void overtime() {
        WorkTimeResult r = calc.calculate(
                LocalDateTime.of(2026, 6, 1, 9, 0),
                LocalDateTime.of(2026, 6, 1, 21, 30), 60);
        assertThat(r.workedMinutes()).isEqualTo(690);
        assertThat(r.overtimeMinutes()).isEqualTo(210);
        assertThat(r.lateNightMinutes()).isZero();
    }

    @Test
    @DisplayName("深夜残業 18:00-24:00 → 22:00-24:00の120分が深夜")
    void lateNightBeforeMidnight() {
        WorkTimeResult r = calc.calculate(
                LocalDateTime.of(2026, 6, 1, 18, 0),
                LocalDateTime.of(2026, 6, 2, 0, 0), 0);
        assertThat(r.lateNightMinutes()).isEqualTo(120);
    }

    @Test
    @DisplayName("夜勤 22:00-翌07:00 → 深夜は22-24(120)+0-5(300)=420分")
    void overnightShift() {
        WorkTimeResult r = calc.calculate(
                LocalDateTime.of(2026, 6, 1, 22, 0),
                LocalDateTime.of(2026, 6, 2, 7, 0), 60);
        assertThat(r.workedMinutes()).isEqualTo(480); // 9h-1h休憩
        assertThat(r.lateNightMinutes()).isEqualTo(420);
    }

    @Test
    @DisplayName("退勤が出勤より前なら例外")
    void invalidOrder() {
        assertThatThrownBy(() -> calc.calculate(
                LocalDateTime.of(2026, 6, 1, 18, 0),
                LocalDateTime.of(2026, 6, 1, 9, 0), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("未打刻(null)なら全て0")
    void notClocked() {
        WorkTimeResult r = calc.calculate(LocalDateTime.of(2026, 6, 1, 9, 0), null, 0);
        assertThat(r.workedMinutes()).isZero();
    }
}
