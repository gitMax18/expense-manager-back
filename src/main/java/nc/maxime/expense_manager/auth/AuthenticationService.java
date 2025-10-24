package nc.maxime.expense_manager.auth;

import nc.maxime.expense_manager.auth.dto.AuthenticationRequestDto;
import nc.maxime.expense_manager.user.Role;
import nc.maxime.expense_manager.user.User;
import nc.maxime.expense_manager.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public AuthenticationService(
                        AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        public User authenticate(AuthenticationRequestDto request) {
                var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
                                request.email(),
                                request.password());
                authenticationManager.authenticate(authenticationToken);

                return userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Utilisateur non trouvé pour " + request.email()));
        }

        public User register(AuthenticationRequestDto request) {
                var user = User.builder()
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .role(Role.USER)
                                .build();

                return userRepository.save(user);
        }
}
