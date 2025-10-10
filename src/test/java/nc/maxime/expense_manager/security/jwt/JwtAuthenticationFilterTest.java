package nc.maxime.expense_manager.security.jwt;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.User;
import nc.maxime.expense_manager.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

        @Mock
        private JwtService jwtService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private HttpServletRequest request;

        @Mock
        private HttpServletResponse response;

        @Mock
        private FilterChain filterChain;

        @InjectMocks
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        private User user;

        @BeforeEach
        void setUp() {
                user = User.builder()
                                .email("john.doe@example.com")
                                .password("secret")
                                .role(Role.USER)
                                .build();
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void shouldSkipWhenAuthorizationHeaderMissing() throws ServletException, IOException {
                when(request.getHeader("Authorization")).thenReturn(null);

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
                verifyNoInteractions(jwtService, userRepository);
                assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void shouldSkipWhenAuthorizationHeaderNotBearer() throws ServletException, IOException {
                when(request.getHeader("Authorization")).thenReturn("Basic xyz");

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
                verifyNoInteractions(jwtService, userRepository);
                assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void shouldSkipWhenAuthenticationAlreadyPresent() throws ServletException, IOException {
                when(request.getHeader("Authorization")).thenReturn("Bearer token");
                var existingAuth = new UsernamePasswordAuthenticationToken("user", "credentials");
                SecurityContextHolder.getContext().setAuthentication(existingAuth);

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
                verifyNoInteractions(jwtService);
                assertSame(existingAuth, SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void shouldAuthenticateWhenTokenValid() throws ServletException, IOException {
                var token = "valid-token";
                when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
                when(jwtService.extractUsername(token)).thenReturn(user.getEmail());
                when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
                when(jwtService.isTokenValid(token, user)).thenReturn(true);

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(jwtService).extractUsername(token);
                verify(jwtService).isTokenValid(token, user);
                verify(userRepository).findByEmail(user.getEmail());
                verify(filterChain).doFilter(request, response);

                var authentication = SecurityContextHolder.getContext().getAuthentication();
                assertTrue(authentication instanceof UsernamePasswordAuthenticationToken);
                assertSame(user, authentication.getPrincipal());
        }

        @Test
        void shouldNotAuthenticateWhenUserNotFound() throws ServletException, IOException {
                var token = "unknown-token";
                when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
                when(jwtService.extractUsername(token)).thenReturn(user.getEmail());
                when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(jwtService).extractUsername(token);
                verify(userRepository).findByEmail(user.getEmail());
                verify(jwtService, never()).isTokenValid(any(), any());
                verify(filterChain).doFilter(request, response);
                assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        void shouldClearContextWhenJwtExceptionThrown() throws ServletException, IOException {
                var token = "invalid-token";
                when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
                when(jwtService.extractUsername(token)).thenThrow(new JwtException("invalid"));

                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

                verify(jwtService).extractUsername(token);
                verify(filterChain).doFilter(request, response);
                assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
}
