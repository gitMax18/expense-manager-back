package nc.maxime.expense_manager.auth;

import nc.maxime.expense_manager.auth.dto.AuthenticationResponse;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequest;
import nc.maxime.expense_manager.security.jwt.JwtService;
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
        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public AuthenticationService(
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
                                request.email(),
                                request.password());
                authenticationManager.authenticate(authenticationToken);

                return userRepository.findByEmail(request.email())
                                .map(user -> new AuthenticationResponse(
                                                user.getEmail(),
                                                user.getRole(),
                                                jwtService.generateToken(user)))
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Utilisateur non trouvé pour " + request.email()));
        }

        public AuthenticationResponse register(AuthenticationRequest request) {
                var user = User.builder()
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .role(Role.USER)
                                .build();

                userRepository.save(user);

                return new AuthenticationResponse(
                                user.getEmail(),
                                user.getRole(),
                                jwtService.generateToken(user));
        }
}
