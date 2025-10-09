package nc.maxime.expense_manager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import nc.maxime.expense_manager.user.Role;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        Role role
) {
}
