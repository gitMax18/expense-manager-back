package nc.maxime.expense_manager.user.dto;

import java.util.Optional;
import nc.maxime.expense_manager.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return Optional.ofNullable(user)
                .map(source -> new UserDto(
                        source.getId(),
                        source.getEmail(),
                        source.getRole(),
                        source.getCreatedAt(),
                        source.getUpdatedAt()))
                .orElseThrow(() -> new IllegalArgumentException("User is required"));
    }
}
