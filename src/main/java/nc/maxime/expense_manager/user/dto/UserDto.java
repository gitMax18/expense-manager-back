package nc.maxime.expense_manager.user.dto;

import java.time.Instant;
import nc.maxime.expense_manager.user.Role;

public record UserDto(
        Long id,
        String email,
        Role role,
        Instant createdAt,
        Instant updatedAt) {
}
