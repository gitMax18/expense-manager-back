package nc.maxime.expense_manager.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequestDto;
import nc.maxime.expense_manager.auth.dto.AuthenticationResponseDto;
import nc.maxime.expense_manager.common.response.AppResponse;
import nc.maxime.expense_manager.config.TestcontainersConfiguration;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerThenLoginShouldSucceed() throws Exception {
        var request = new AuthenticationRequestDto("integration.user@example.com", "Password123!");

        var registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        var registerBody = registerResult.getResponse().getContentAsString();
        var registerResponse = objectMapper.readValue(registerBody,
                new TypeReference<AppResponse<AuthenticationResponseDto>>() {
                });

        assertThat(registerResponse.data().user().email()).isEqualTo(request.email());
        assertThat(registerResponse.data().user().role()).isEqualTo(Role.USER);
        assertThat(registerResponse.data().token()).isNotBlank();

        var persistedUser = userRepository.findByEmail(request.email()).orElseThrow();
        assertThat(passwordEncoder.matches(request.password(), persistedUser.getPassword())).isTrue();
        assertThat(persistedUser.getPassword()).isNotEqualTo(request.password());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        var loginBody = loginResult.getResponse().getContentAsString();
        var loginResponse = objectMapper.readValue(loginBody,
                new TypeReference<AppResponse<AuthenticationResponseDto>>() {
                });

        assertThat(loginResponse.data().user().email()).isEqualTo(request.email());
        assertThat(loginResponse.data().user().role()).isEqualTo(Role.USER);
        assertThat(loginResponse.data().token()).isNotBlank();
    }
}
