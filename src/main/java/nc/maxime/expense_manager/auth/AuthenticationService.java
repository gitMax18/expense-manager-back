package nc.maxime.expense_manager.auth;

import java.util.Optional;
import nc.maxime.expense_manager.auth.dto.AuthenticationRequestDto;
import nc.maxime.expense_manager.category.TransactionCategoryService;
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
        private final TransactionCategoryService transactionCategoryService;

        public AuthenticationService(
                        AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TransactionCategoryService transactionCategoryService) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.transactionCategoryService = transactionCategoryService;
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
                return Optional.ofNullable(request)
                                .map(payload -> User.builder()
                                                .email(payload.email())
                                                .password(passwordEncoder.encode(payload.password()))
                                                .role(Role.USER)
                                                .build())
                                .map(userRepository::save)
                                .map(savedUser -> {
                                        transactionCategoryService.createDefaultCategories(savedUser);
                                        return savedUser;
                                })
                                .orElseThrow(() -> new IllegalArgumentException("Invalid registration payload"));
        }
}
