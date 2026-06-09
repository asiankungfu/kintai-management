package com.example.kintai.web;

import com.example.kintai.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** トップ：社員一覧。 */
@Controller
public class DashboardController {

    private final EmployeeRepository employeeRepository;

    public DashboardController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "index";
    }
}
