package com.example.kintai.config;

import com.example.kintai.service.WorkTimeCalculator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** DI構成。就業規則の設定から労働時間計算器を組み立てる。 */
@Configuration
@EnableConfigurationProperties(WorkRuleProperties.class)
public class AppConfig {

    @Bean
    public WorkTimeCalculator workTimeCalculator(WorkRuleProperties props) {
        return new WorkTimeCalculator(props.getStandardWorkMinutes());
    }
}
