package com.example.kintai.service;

/**
 * 労働時間の計算結果（分単位）。
 *
 * @param workedMinutes    実労働時間（拘束時間 − 休憩）
 * @param overtimeMinutes  残業時間（所定労働時間を超えた分）
 * @param lateNightMinutes 深夜時間（22:00〜翌05:00に働いた分）
 */
public record WorkTimeResult(long workedMinutes, long overtimeMinutes, long lateNightMinutes) {

    /** 分を "H:MM" 形式に整形する。 */
    public static String toHhmm(long minutes) {
        long h = minutes / 60;
        long m = minutes % 60;
        return h + ":" + String.format("%02d", m);
    }

    public String workedLabel() {
        return toHhmm(workedMinutes);
    }

    public String overtimeLabel() {
        return toHhmm(overtimeMinutes);
    }

    public String lateNightLabel() {
        return toHhmm(lateNightMinutes);
    }
}
