package com.example.kintai.service;

import com.example.kintai.domain.ApprovalRequest;
import com.example.kintai.domain.RequestStatus;
import com.example.kintai.repository.ApprovalRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 申請の承認ワークフローを管理するサービス。
 *
 * <p>状態遷移は PENDING からのみ可能：
 * PENDING → APPROVED / REJECTED（承認者）、PENDING → CANCELLED（申請者）。
 * それ以外の遷移は {@link IllegalStateException} で弾く（不正な二重承認等を防止）。</p>
 */
@Service
public class ApprovalService {

    private final ApprovalRequestRepository repository;

    public ApprovalService(ApprovalRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ApprovalRequest submit(ApprovalRequest request) {
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    @Transactional
    public ApprovalRequest approve(Long requestId, Long approverId, String comment) {
        ApprovalRequest req = find(requestId);
        requirePending(req);
        req.setStatus(RequestStatus.APPROVED);
        req.setApproverId(approverId);
        req.setDecisionComment(comment);
        req.setDecidedAt(LocalDateTime.now());
        return repository.save(req);
    }

    @Transactional
    public ApprovalRequest reject(Long requestId, Long approverId, String comment) {
        ApprovalRequest req = find(requestId);
        requirePending(req);
        req.setStatus(RequestStatus.REJECTED);
        req.setApproverId(approverId);
        req.setDecisionComment(comment);
        req.setDecidedAt(LocalDateTime.now());
        return repository.save(req);
    }

    @Transactional
    public ApprovalRequest cancel(Long requestId, Long applicantId) {
        ApprovalRequest req = find(requestId);
        requirePending(req);
        if (!req.getApplicantId().equals(applicantId)) {
            throw new IllegalStateException("申請者本人のみ取下げできます");
        }
        req.setStatus(RequestStatus.CANCELLED);
        req.setDecidedAt(LocalDateTime.now());
        return repository.save(req);
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequest> pendingRequests() {
        return repository.findByStatusOrderByCreatedAtAsc(RequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequest> myRequests(Long applicantId) {
        return repository.findByApplicantIdOrderByCreatedAtDesc(applicantId);
    }

    private ApprovalRequest find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("申請が見つかりません: id=" + id));
    }

    private void requirePending(ApprovalRequest req) {
        if (req.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "この申請は既に「" + req.getStatus().getLabel() + "」のため操作できません");
        }
    }
}
