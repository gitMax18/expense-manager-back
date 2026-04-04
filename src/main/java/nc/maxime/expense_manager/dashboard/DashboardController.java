package nc.maxime.expense_manager.dashboard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.dashboard.dto.CategoryExpenseDto;
import nc.maxime.expense_manager.dashboard.dto.DashboardMapper;
import nc.maxime.expense_manager.user.User;

@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardMapper dashboardMapper;

    public DashboardController(DashboardService dashboardService, DashboardMapper dashboardMapper) {
        this.dashboardService = dashboardService;
        this.dashboardMapper = dashboardMapper;
    }

    @GetMapping("/expenses-per-category")
    public ResponseEntity<AppResponse<List<CategoryExpenseDto>>> getExpensesByCategory(
            @NotNull @AuthenticationPrincipal User user) {
        var expenses = dashboardService.getExpenseTotalsByCategory(user).stream()
                .map(dashboardMapper::toCategoryExpenseDto)
                .toList();

        return ResponseEntity.ok(AppResponse.message("Expense totals by category retrieved").data(expenses));
    }
}
