package com.example.kintai.domain;

/** 申請種別。 */
public enum RequestType {
    /** 残業申請。 */
    OVERTIME("残業"),
    /** 有給休暇。 */
    PAID_LEAVE("有給休暇"),
    /** 打刻修正。 */
    ATTENDANCE_CORRECTION("打刻修正");

    private final String label;

    RequestType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
