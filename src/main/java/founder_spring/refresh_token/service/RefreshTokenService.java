package founder_spring.refresh_token.service;

import founder_spring.common.util.CuidGenerator;
import founder_spring.refresh_token.entity.RefreshToken;
import founder_spring.refresh_token.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;
    private static final int EXPIRES_IN_DAYS = 30;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CuidGenerator cuidGenerator;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            CuidGenerator cuidGenerator
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.cuidGenerator = cuidGenerator;
    }

    @Transactional
    public String create(
            String userId,
            String accountId
    ) {

        String rawToken = generateToken();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setId(cuidGenerator.generate());
        refreshToken.setUserId(userId);
        refreshToken.setAccountId(accountId);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(EXPIRES_IN_DAYS)
        );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken findValidToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository.findByTokenHash(tokenHash)
                        .orElse(null);

        if (refreshToken == null) {
            return null;
        }

        if (refreshToken.getRevokedAt() != null) {
            return null;
        }

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            return null;
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {

        refreshToken.setRevokedAt(
                LocalDateTime.now()
        );

        refreshTokenRepository.save(refreshToken);
    }

    private String generateToken() {

        byte[] bytes = new byte[TOKEN_BYTES];

        new SecureRandom().nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(token.getBytes());

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }
}