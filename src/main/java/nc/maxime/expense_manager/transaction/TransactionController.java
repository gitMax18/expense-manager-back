package nc.maxime.expense_manager.transaction;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.transaction.dto.TransactionDto;
import nc.maxime.expense_manager.transaction.dto.TransactionMapper;
import nc.maxime.expense_manager.transaction.dto.UpsertTransactionDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<AppResponse<TransactionDto>> createTransaction(
            @Valid @RequestBody UpsertTransactionDto request) {
        var transaction = transactionService.createTransaction(request);
        var response = transactionMapper.toDto(transaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.message("Transaction created").data(response));
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<TransactionDto>>> getTransactions(
            @NotNull @AuthenticationPrincipal User user,
            @RequestParam(value = "accountId", required = false) Long accountId) {
        var transactions = transactionService.getTransactions(user, accountId).stream()
                .map(transactionMapper::toDto)
                .toList();
        return ResponseEntity.ok(AppResponse.message("Transactions retrieved").data(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<TransactionDto>> getTransaction(@PathVariable Long id) {
        var transaction = transactionService.getTransaction(id);
        var response = transactionMapper.toDto(transaction);
        return ResponseEntity.ok(AppResponse.message("Transaction retrieved").data(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<TransactionDto>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpsertTransactionDto request) {
        var transaction = transactionService.updateTransaction(id, request);
        var response = transactionMapper.toDto(transaction);
        return ResponseEntity.ok(AppResponse.message("Transaction updated").data(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(AppResponse.message("Transaction deleted").data(null));
    }
}
