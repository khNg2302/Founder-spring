package founder_spring.auth.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
        String userId,
        String accountId,
        String verificationToken,
        LocalDateTime expiresAt
) {
}