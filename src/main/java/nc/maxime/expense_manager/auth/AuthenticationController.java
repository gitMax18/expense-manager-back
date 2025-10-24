package nc.maxime.expense_manager.auth;

import jakarta.validation.Valid;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequestDto;
import nc.maxime.expense_manager.auth.dto.AuthenticationResponseDto;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.security.jwt.JwtService;
import nc.maxime.expense_manager.user.dto.UserMapper;
import org.springframework.http.ResponseEntity;
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
        private final JwtService jwtService;
        private final UserMapper userMapper;

        public AuthenticationController(
                        AuthenticationService authenticationService,
                        JwtService jwtService,
                        UserMapper userMapper) {
                this.authenticationService = authenticationService;
                this.jwtService = jwtService;
                this.userMapper = userMapper;
        }

        @PostMapping("/login")
        public ResponseEntity<AppResponse<AuthenticationResponseDto>> login(
                        @Valid @RequestBody AuthenticationRequestDto request) {
                var user = authenticationService.authenticate(request);
                var token = jwtService.generateToken(user);
                var response = new AuthenticationResponseDto(userMapper.toDto(user), token);
                return ResponseEntity.ok(AppResponse.message("Authentication successful").data(response));
        }

        @PostMapping("/register")
        public ResponseEntity<AppResponse<AuthenticationResponseDto>> register(
                        @Valid @RequestBody AuthenticationRequestDto request) {
                var user = authenticationService.register(request);
                var token = jwtService.generateToken(user);
                var response = new AuthenticationResponseDto(userMapper.toDto(user), token);
                return ResponseEntity.ok(AppResponse.message("Registration successful").data(response));
        }
}
