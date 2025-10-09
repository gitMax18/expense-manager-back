package nc.maxime.expense_manager.auth.dto;

import nc.maxime.expense_manager.user.Role;

public record AuthenticationResponse(
        String email,
        Role role,
        String token
) {
}
