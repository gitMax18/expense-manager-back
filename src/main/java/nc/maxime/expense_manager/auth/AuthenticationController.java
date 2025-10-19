package nc.maxime.expense_manager.auth;

import jakarta.validation.Valid;
import nc.maxime.expense_manager.auth.dto.AuthenticationResponse;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequest;
import org.springframework.http.ResponseEntity;
import nc.maxime.expense_manager.common.response.AppResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AppResponse<AuthenticationResponse>> login(
            @Valid @RequestBody AuthenticationRequest request) {
        var response = authenticationService.authenticate(request);
        return ResponseEntity.ok(new AppResponse<AuthenticationResponse>("Authentification successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<AppResponse<AuthenticationResponse>> register(
            @Valid @RequestBody AuthenticationRequest request) {
        var response = authenticationService.register(request);
        return ResponseEntity.ok(new AppResponse<AuthenticationResponse>("Registration successful", response));
    }
}
