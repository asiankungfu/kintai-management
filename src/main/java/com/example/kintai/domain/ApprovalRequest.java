package com.example.kintai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 承認申請（残業・休暇・打刻修正）。状態遷移は {@code ApprovalService} が管理する。
 */
@Entity
@Table(name = "approval_request")
@Getter
@Setter
@NoArgsConstructor
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 申請者。 */
    @Column(nullable = false)
    private Long applicantId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType type;

    /** 対象日。 */
    @Column(nullable = false)
    private LocalDate targetDate;

    /** 申請時間（分）。残業申請なら残業見込み分など。 */
    private int minutes;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    /** 承認者。 */
    private Long approverId;

    /** 承認/却下コメント。 */
    @Column(length = 500)
    private String decisionComment;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime decidedAt;
}
