package com.example.kintai.web;

import com.example.kintai.domain.ApprovalRequest;
import com.example.kintai.domain.Employee;
import com.example.kintai.domain.RequestType;
import com.example.kintai.repository.EmployeeRepository;
import com.example.kintai.service.ApprovalService;
import com.example.kintai.service.ResourceNotFoundException;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 申請・承認ワークフローの画面。 */
@Controller
public class ApprovalController {

    private final ApprovalService approvalService;
    private final EmployeeRepository employeeRepository;

    public ApprovalController(ApprovalService approvalService,
                              EmployeeRepository employeeRepository) {
        this.approvalService = approvalService;
        this.employeeRepository = employeeRepository;
    }

    /** 自分の申請一覧＋新規申請フォーム。 */
    @GetMapping("/employees/{empId}/requests")
    public String myRequests(@PathVariable Long empId, Model model) {
        Employee emp = employee(empId);
        model.addAttribute("employee", emp);
        model.addAttribute("requests", approvalService.myRequests(empId));
        model.addAttribute("types", RequestType.values());
        return "requests";
    }

    @PostMapping("/employees/{empId}/requests")
    public String submit(@PathVariable Long empId,
                         @RequestParam RequestType type,
                         @RequestParam LocalDate targetDate,
                         @RequestParam(defaultValue = "0") int minutes,
                         @RequestParam(required = false) String reason) {
        ApprovalRequest req = new ApprovalRequest();
        req.setApplicantId(empId);
        req.setType(type);
        req.setTargetDate(targetDate);
        req.setMinutes(minutes);
        req.setReason(reason);
        approvalService.submit(req);
        return "redirect:/employees/" + empId + "/requests";
    }

    @PostMapping("/employees/{empId}/requests/{id}/cancel")
    public String cancel(@PathVariable Long empId, @PathVariable Long id) {
        approvalService.cancel(id, empId);
        return "redirect:/employees/" + empId + "/requests";
    }

    /** 承認待ち一覧（承認者向け）。 */
    @GetMapping("/approvals")
    public String approvals(@RequestParam Long approverId, Model model) {
        model.addAttribute("approver", employee(approverId));
        model.addAttribute("pending", approvalService.pendingRequests());
        return "approvals";
    }

    @PostMapping("/approvals/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam Long approverId,
                          @RequestParam(required = false) String comment) {
        approvalService.approve(id, approverId, comment);
        return "redirect:/approvals?approverId=" + approverId;
    }

    @PostMapping("/approvals/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam Long approverId,
                         @RequestParam(required = false) String comment) {
        approvalService.reject(id, approverId, comment);
        return "redirect:/approvals?approverId=" + approverId;
    }

    private Employee employee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("社員が見つかりません: id=" + id));
    }
}
