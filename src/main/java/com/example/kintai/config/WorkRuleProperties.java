package com.example.kintai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 就業規則に関する設定（application.yml の kintai.work-rule.*）。
 */
@ConfigurationProperties(prefix = "kintai.work-rule")
public class WorkRuleProperties {

    /** 所定労働時間（分）。既定は8時間=480分。 */
    private int standardWorkMinutes = 480;

    public int getStandardWorkMinutes() {
        return standardWorkMinutes;
    }

    public void setStandardWorkMinutes(int standardWorkMinutes) {
        this.standardWorkMinutes = standardWorkMinutes;
    }
}
