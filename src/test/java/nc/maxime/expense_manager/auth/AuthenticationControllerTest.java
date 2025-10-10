package nc.maxime.expense_manager.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequest;
import nc.maxime.expense_manager.auth.dto.AuthenticationResponse;
import nc.maxime.expense_manager.security.SecurityConfig;
import nc.maxime.expense_manager.security.jwt.JwtAuthenticationFilter;
import nc.maxime.expense_manager.security.jwt.JwtService;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class })
@TestPropertySource(properties = {
                "security.jwt.secret=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                "security.jwt.expiration-seconds=3600"
})
class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthenticationService authenticationService;

        @MockitoBean
        private UserRepository userRepository;

        @Test
        void loginShouldReturnTokenWhenCredentialsValid() throws Exception {
                var request = new AuthenticationRequest("john.doe@example.com", "secret");
                var response = new AuthenticationResponse(request.email(), Role.USER, "jwt-token");
                when(authenticationService.authenticate(request)).thenReturn(response);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(response)));

                verify(authenticationService).authenticate(request);
        }

        @Test
        void registerShouldReturnTokenWhenUserCreated() throws Exception {
                var request = new AuthenticationRequest("jane.doe@example.com", "secret");
                var response = new AuthenticationResponse(request.email(), Role.USER, "jwt-token");
                when(authenticationService.register(request)).thenReturn(response);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(response)));

                verify(authenticationService).register(request);
        }

        @Test
        void loginShouldReturnForbiddenWhenServiceThrows() throws Exception {
                var request = new AuthenticationRequest("missing.user@example.com", "secret");
                when(authenticationService.authenticate(request))
                                .thenThrow(new UsernameNotFoundException("missing"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden())
                                .andExpect(content().string(""));

                verify(authenticationService).authenticate(request);
        }
}
