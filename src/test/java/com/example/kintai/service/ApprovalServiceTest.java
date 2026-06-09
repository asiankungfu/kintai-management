package com.example.kintai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.kintai.domain.ApprovalRequest;
import com.example.kintai.domain.RequestStatus;
import com.example.kintai.repository.ApprovalRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private ApprovalRequestRepository repository;

    @InjectMocks
    private ApprovalService service;

    private ApprovalRequest pending;

    @BeforeEach
    void setUp() {
        pending = new ApprovalRequest();
        pending.setId(1L);
        pending.setApplicantId(10L);
        pending.setStatus(RequestStatus.PENDING);
        when(repository.save(any(ApprovalRequest.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("PENDINGの申請は承認できAPPROVEDになる")
    void approve() {
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        ApprovalRequest r = service.approve(1L, 99L, "OK");
        assertThat(r.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(r.getApproverId()).isEqualTo(99L);
        assertThat(r.getDecidedAt()).isNotNull();
    }

    @Test
    @DisplayName("承認済みの申請を再度承認しようとすると例外（二重承認の防止）")
    void cannotApproveTwice() {
        pending.setStatus(RequestStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        assertThatThrownBy(() -> service.approve(1L, 99L, "again"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("申請者本人以外は取下げできない")
    void cancelOnlyByApplicant() {
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        assertThatThrownBy(() -> service.cancel(1L, 999L))
                .isInstanceOf(IllegalStateException.class);
    }
}
