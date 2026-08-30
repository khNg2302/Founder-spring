package founder_spring.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}