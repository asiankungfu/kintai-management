package com.example.kintai.domain;

/** 権限ロール。 */
public enum Role {
    /** 一般社員（自分の打刻・申請）。 */
    EMPLOYEE,
    /** 管理者（部下の申請を承認）。 */
    MANAGER
}
