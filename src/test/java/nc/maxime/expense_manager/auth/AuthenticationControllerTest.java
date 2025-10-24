package nc.maxime.expense_manager.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequestDto;
import nc.maxime.expense_manager.security.jwt.JwtService;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.User;
import nc.maxime.expense_manager.user.UserRepository;
import nc.maxime.expense_manager.user.dto.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserMapper.class)
@ActiveProfiles("test")
class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthenticationService authenticationService;

        @MockitoBean
        private UserRepository userRepository;

        @MockitoBean
        private JwtService jwtService;

        @Test
        void loginShouldReturnTokenWhenCredentialsValid() throws Exception {
                var request = new AuthenticationRequestDto("john.doe@example.com", "secret");
                var user = User.builder()
                                .email(request.email())
                                .role(Role.USER)
                                .password("hashed")
                                .build();
                when(authenticationService.authenticate(request)).thenReturn(user);
                when(jwtService.generateToken(user)).thenReturn("jwt-token");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Authentication successful"))
                                .andExpect(jsonPath("$.data.user.email").value(request.email()))
                                .andExpect(jsonPath("$.data.user.role").value(Role.USER.name()))
                                .andExpect(jsonPath("$.data.token").value("jwt-token"));

                verify(authenticationService).authenticate(request);
                verify(jwtService).generateToken(user);
        }

        @Test
        void registerShouldReturnTokenWhenUserCreated() throws Exception {
                var request = new AuthenticationRequestDto("jane.doe@example.com", "secret");
                var user = User.builder()
                                .email(request.email())
                                .role(Role.USER)
                                .password("hashed")
                                .build();
                when(authenticationService.register(request)).thenReturn(user);
                when(jwtService.generateToken(user)).thenReturn("jwt-token");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Registration successful"))
                                .andExpect(jsonPath("$.data.user.email").value(request.email()))
                                .andExpect(jsonPath("$.data.user.role").value(Role.USER.name()))
                                .andExpect(jsonPath("$.data.token").value("jwt-token"));

                verify(authenticationService).register(request);
                verify(jwtService).generateToken(user);
        }

        @Test
        void loginShouldReturnForbiddenWhenServiceThrows() throws Exception {
                var request = new AuthenticationRequestDto("missing.user@example.com", "secret");
                when(authenticationService.authenticate(request))
                                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(authenticationService).authenticate(request);
        }
}
