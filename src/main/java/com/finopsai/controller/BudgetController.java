package com.finopsai.controller;

import com.finopsai.dto.BudgetRequest;
import com.finopsai.dto.BudgetResponse;
import com.finopsai.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
public class BudgetController {
    private final BudgetService budgetService;
    public BudgetController(BudgetService budgetService) { this.budgetService = budgetService; }
    @GetMapping("/budgets") public String page(Model model, Principal principal) { model.addAttribute("budgets", budgetService.list(principal.getName())); return "budgets"; }
    @PostMapping("/budgets") public String create(@Valid BudgetRequest request, Principal principal, RedirectAttributes flash) { budgetService.create(request, principal.getName()); flash.addFlashAttribute("success", "Budget created."); return "redirect:/budgets"; }
    @PostMapping("/budgets/{id}/delete") public String delete(@PathVariable Long id, Principal principal, RedirectAttributes flash) { budgetService.delete(id, principal.getName()); flash.addFlashAttribute("success", "Budget removed."); return "redirect:/budgets"; }
    @ResponseBody @GetMapping("/api/budgets") public List<BudgetResponse> list(Principal principal) { return budgetService.list(principal.getName()); }
    @ResponseBody @ResponseStatus(HttpStatus.CREATED) @PostMapping("/api/budgets") public BudgetResponse createApi(@Valid @RequestBody BudgetRequest request, Principal principal) { return budgetService.create(request, principal.getName()); }
    @ResponseBody @ResponseStatus(HttpStatus.NO_CONTENT) @DeleteMapping("/api/budgets/{id}") public void deleteApi(@PathVariable Long id, Principal principal) { budgetService.delete(id, principal.getName()); }
}
