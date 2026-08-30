package founder_spring.refresh_token.service;

public record CreatedRefreshToken(
        String token,
        String sessionId
) {
}