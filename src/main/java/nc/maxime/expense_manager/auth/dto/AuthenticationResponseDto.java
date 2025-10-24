package nc.maxime.expense_manager.auth.dto;

import nc.maxime.expense_manager.user.dto.UserDto;

public record AuthenticationResponseDto(
        UserDto user,
        String token
) {
}
