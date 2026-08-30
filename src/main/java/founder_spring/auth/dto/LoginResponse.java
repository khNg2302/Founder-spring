package founder_spring.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}