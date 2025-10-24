package nc.maxime.expense_manager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(
                @NotBlank @Email String email,
                @NotBlank String password) {
}
