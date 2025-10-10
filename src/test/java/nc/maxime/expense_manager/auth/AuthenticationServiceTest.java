package nc.maxime.expense_manager.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequest;
import nc.maxime.expense_manager.auth.dto.AuthenticationResponse;
import nc.maxime.expense_manager.security.jwt.JwtService;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.User;
import nc.maxime.expense_manager.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private JwtService jwtService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private AuthenticationService authenticationService;

        @Captor
        private ArgumentCaptor<User> userCaptor;

        @Captor
        private ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenCaptor;

        @Test
        void authenticateShouldReturnResponseWhenUserExists() {
                var request = new AuthenticationRequest("john.doe@example.com", "secret");
                var existingUser = User.builder()
                                .email(request.email())
                                .password("hashed-password")
                                .role(Role.USER)
                                .build();
                when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));
                when(jwtService.generateToken(existingUser)).thenReturn("jwt-token");

                AuthenticationResponse response = authenticationService.authenticate(request);

                verify(authenticationManager).authenticate(authenticationTokenCaptor.capture());
                var capturedToken = authenticationTokenCaptor.getValue();
                assertEquals(request.email(), capturedToken.getPrincipal());
                assertEquals(request.password(), capturedToken.getCredentials());

                assertEquals(existingUser.getEmail(), response.email());
                assertEquals(existingUser.getRole(), response.role());
                assertEquals("jwt-token", response.token());
        }

        @Test
        void authenticateShouldThrowWhenUserNotFound() {
                var request = new AuthenticationRequest("unknown@example.com", "secret");
                when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

                assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
        }

        @Test
        void registerShouldPersistUserAndReturnResponse() {
                var request = new AuthenticationRequest("new.user@example.com", "secret");
                when(passwordEncoder.encode(request.password())).thenReturn("encoded-secret");
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

                AuthenticationResponse response = authenticationService.register(request);

                verify(userRepository).save(userCaptor.capture());
                var savedUser = userCaptor.getValue();
                assertEquals(request.email(), savedUser.getEmail());
                assertEquals("encoded-secret", savedUser.getPassword());
                assertEquals(Role.USER, savedUser.getRole());

                assertEquals(savedUser.getEmail(), response.email());
                assertEquals(savedUser.getRole(), response.role());
                assertEquals("jwt-token", response.token());

                verify(jwtService).generateToken(savedUser);
        }
}
