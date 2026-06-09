package com.example.kintai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 勤怠管理システムのエントリポイント。
 *
 * <p>社員の打刻から労働時間・残業時間・深夜時間を自動計算し、
 * 残業・休暇などの申請承認フローと月次集計（CSV出力）を提供する。</p>
 */
@SpringBootApplication
public class KintaiManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(KintaiManagementApplication.class, args);
    }
}
