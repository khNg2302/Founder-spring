package founder_spring.auth.service;

public record OAuthLoginResult(
        String accessToken,
        String refreshToken
) {
}