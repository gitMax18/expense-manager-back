package nc.maxime.expense_manager.account;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.account.dto.AccountDto;
import nc.maxime.expense_manager.account.dto.AccountMapper;
import nc.maxime.expense_manager.account.dto.UpsertAccountDto;
import nc.maxime.expense_manager.common.response.AppResponse;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<AppResponse<AccountDto>> createAccount(
            @NotNull @AuthenticationPrincipal User user,
            @Valid @RequestBody UpsertAccountDto request) {
        var account = accountService.createAccount(user, request);
        var response = accountMapper.toDto(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppResponse.message("New account created").data(response));
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<AccountDto>>> getUserAccounts(@AuthenticationPrincipal User user) {
        List<AccountDto> accounts = accountService.getUserAccounts(user).stream()
                .map(accountMapper::toDto)
                .toList();
        return ResponseEntity.ok().body(AppResponse.message("User accounts send").data(accounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<AccountDto>> getAccountById(@PathVariable Long id) {
        var account = accountService.getAccount(id);
        var response = accountMapper.toDto(account);
        return ResponseEntity.ok().body(AppResponse.message("Account find").data(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<AccountDto>> updateAccount(
            @NotNull @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpsertAccountDto request) {
        var account = accountService.updateAccount(id, request);
        var response = accountMapper.toDto(account);
        return ResponseEntity.ok(AppResponse.message("Account updated").data(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<Void>> deleteAccount(
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(AppResponse.message("Account deleted").data(null));
    }
}
