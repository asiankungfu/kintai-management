package com.example.kintai.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 出退勤の打刻から労働時間・残業時間・深夜時間を算出するドメインロジック。
 *
 * <p>仕様</p>
 * <ul>
 *   <li>実労働時間 = (退勤 − 出勤) − 休憩。</li>
 *   <li>残業時間 = max(0, 実労働時間 − 所定労働時間)（既定 8h=480分）。</li>
 *   <li>深夜時間 = 勤務区間のうち 22:00〜翌05:00 に重なる分（労基法の深夜割増に対応）。</li>
 *   <li>退勤が翌日にまたがる夜勤にも対応（LocalDateTime で日付差を含めて計算）。</li>
 * </ul>
 *
 * <p>副作用なしの純粋計算クラス。フレームワーク非依存のため単体テストが容易。
 * 所定労働時間は設定値から {@code AppConfig} がBeanとして注入する。</p>
 */
public class WorkTimeCalculator {

    private static final LocalTime NIGHT_START = LocalTime.of(22, 0); // 22:00
    private static final LocalTime NIGHT_END = LocalTime.of(5, 0);    // 翌05:00

    private final int standardWorkMinutes;

    public WorkTimeCalculator(int standardWorkMinutes) {
        this.standardWorkMinutes = standardWorkMinutes;
    }

    public WorkTimeResult calculate(LocalDateTime clockIn, LocalDateTime clockOut, int breakMinutes) {
        if (clockIn == null || clockOut == null) {
            return new WorkTimeResult(0, 0, 0);
        }
        if (clockOut.isBefore(clockIn)) {
            throw new IllegalArgumentException("退勤時刻が出勤時刻より前です");
        }
        if (breakMinutes < 0) {
            throw new IllegalArgumentException("休憩時間が負です");
        }

        long span = Duration.between(clockIn, clockOut).toMinutes();
        long worked = Math.max(0, span - breakMinutes);
        long overtime = Math.max(0, worked - standardWorkMinutes);
        long lateNight = lateNightMinutes(clockIn, clockOut);
        return new WorkTimeResult(worked, overtime, lateNight);
    }

    /**
     * 勤務区間 [in, out] のうち、各日の深夜帯 [22:00, 翌05:00) に重なる分数を合計する。
     * 各日について「早朝帯 00:00〜05:00」と「深夜帯 22:00〜24:00」に分けて重なりを取る。
     */
    long lateNightMinutes(LocalDateTime in, LocalDateTime out) {
        long total = 0;
        LocalDate d = in.toLocalDate();
        LocalDate last = out.toLocalDate();
        while (!d.isAfter(last)) {
            LocalDateTime earlyStart = d.atStartOfDay();
            LocalDateTime earlyEnd = d.atTime(NIGHT_END);
            total += overlapMinutes(in, out, earlyStart, earlyEnd);

            LocalDateTime lateStart = d.atTime(NIGHT_START);
            LocalDateTime lateEnd = d.plusDays(1).atStartOfDay();
            total += overlapMinutes(in, out, lateStart, lateEnd);

            d = d.plusDays(1);
        }
        return total;
    }

    private long overlapMinutes(LocalDateTime aStart, LocalDateTime aEnd,
                                LocalDateTime bStart, LocalDateTime bEnd) {
        LocalDateTime start = aStart.isAfter(bStart) ? aStart : bStart;
        LocalDateTime end = aEnd.isBefore(bEnd) ? aEnd : bEnd;
        if (end.isAfter(start)) {
            return Duration.between(start, end).toMinutes();
        }
        return 0;
    }
}
