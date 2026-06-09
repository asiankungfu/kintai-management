package com.example.kintai.domain;

/** 申請ステータス（承認ワークフローの状態）。 */
public enum RequestStatus {
    /** 申請中。 */
    PENDING("申請中"),
    /** 承認済み。 */
    APPROVED("承認済み"),
    /** 却下。 */
    REJECTED("却下"),
    /** 取下げ。 */
    CANCELLED("取下げ");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
