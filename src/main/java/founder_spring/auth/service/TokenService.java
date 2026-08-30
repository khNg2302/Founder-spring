package founder_spring.auth.service;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    private static final long ACCESS_TOKEN_EXPIRES_IN_SECONDS = 15 * 60;

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(
            String userId,
            String accountId,
            String sessionId
    ) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId)
                .claim("accountId", accountId)
                .claim("sessionId", sessionId)
                .issuedAt(now)
                .expiresAt(
                        now.plusSeconds(
                                ACCESS_TOKEN_EXPIRES_IN_SECONDS
                        )
                )
                .build();

        JwsHeader header = JwsHeader.with(
                org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256
        ).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}