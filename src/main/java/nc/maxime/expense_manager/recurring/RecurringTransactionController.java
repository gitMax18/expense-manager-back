package nc.maxime.expense_manager.recurring;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.recurring.dto.RecurringTransactionDto;
import nc.maxime.expense_manager.recurring.dto.RecurringTransactionMapper;
import nc.maxime.expense_manager.recurring.dto.UpsertRecurringTransactionDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recuring-transactions")
@Validated
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;
    private final RecurringTransactionMapper recurringTransactionMapper;

    public RecurringTransactionController(
            RecurringTransactionService recurringTransactionService,
            RecurringTransactionMapper recurringTransactionMapper) {
        this.recurringTransactionService = recurringTransactionService;
        this.recurringTransactionMapper = recurringTransactionMapper;
    }

    @PostMapping
    public ResponseEntity<AppResponse<RecurringTransactionDto>> createRecurringTransaction(
            @Valid @RequestBody UpsertRecurringTransactionDto request) {
        var recurringTransaction = recurringTransactionService.createRecurringTransaction(request);
        var response = recurringTransactionMapper.toDto(recurringTransaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.message("Recurring transaction created").data(response));
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<RecurringTransactionDto>>> getRecurringTransactions(
            @NotNull @AuthenticationPrincipal User user,
            @RequestParam(value = "accountId", required = false) Long accountId) {
        var recurringTransactions = recurringTransactionService.getRecurringTransactions(user, accountId).stream()
                .map(recurringTransactionMapper::toDto)
                .toList();
        return ResponseEntity.ok(AppResponse.message("Recurring transactions retrieved").data(recurringTransactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<RecurringTransactionDto>> getRecurringTransaction(@PathVariable Long id) {
        var recurringTransaction = recurringTransactionService.getRecurringTransaction(id);
        var response = recurringTransactionMapper.toDto(recurringTransaction);
        return ResponseEntity.ok(AppResponse.message("Recurring transaction retrieved").data(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<RecurringTransactionDto>> updateRecurringTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpsertRecurringTransactionDto request) {
        var recurringTransaction = recurringTransactionService.updateRecurringTransaction(id, request);
        var response = recurringTransactionMapper.toDto(recurringTransaction);
        return ResponseEntity.ok(AppResponse.message("Recurring transaction updated").data(response));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<AppResponse<RecurringTransactionDto>> pauseRecurringTransaction(@PathVariable Long id) {
        var recurringTransaction = recurringTransactionService.pauseRecurringTransaction(id);
        var response = recurringTransactionMapper.toDto(recurringTransaction);
        return ResponseEntity.ok(AppResponse.message("Recurring transaction paused").data(response));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<AppResponse<RecurringTransactionDto>> resumeRecurringTransaction(@PathVariable Long id) {
        var recurringTransaction = recurringTransactionService.resumeRecurringTransaction(id);
        var response = recurringTransactionMapper.toDto(recurringTransaction);
        return ResponseEntity.ok(AppResponse.message("Recurring transaction resumed").data(response));
    }
}
