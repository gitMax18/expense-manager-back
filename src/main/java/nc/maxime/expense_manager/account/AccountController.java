package nc.maxime.expense_manager.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nc.maxime.expense_manager.account.dto.AccountResponse;
import nc.maxime.expense_manager.account.dto.CreateAccountDto;
import nc.maxime.expense_manager.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @NotNull @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAccountDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(user, request));
    }
}
