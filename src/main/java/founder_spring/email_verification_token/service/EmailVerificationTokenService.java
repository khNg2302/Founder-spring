package founder_spring.email_verification_token.service;

import founder_spring.auth.service.OpaqueTokenService;
import founder_spring.common.util.CuidGenerator;
import founder_spring.email_verification_token.entity.EmailVerificationToken;
import founder_spring.email_verification_token.repository.EmailVerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailVerificationTokenService {

    private static final long EXPIRES_IN_MINUTES = 15;

    private final EmailVerificationTokenRepository repository;
    private final OpaqueTokenService opaqueTokenService;
    private final CuidGenerator cuidGenerator;

    public EmailVerificationTokenService(
            EmailVerificationTokenRepository repository,
            OpaqueTokenService opaqueTokenService,
            CuidGenerator cuidGenerator
    ) {
        this.repository = repository;
        this.opaqueTokenService = opaqueTokenService;
        this.cuidGenerator = cuidGenerator;
    }

    @Transactional
    public CreatedVerificationToken create(String accountId) {

        String tokenId = cuidGenerator.generate();
        String secret = opaqueTokenService.generateSecret();

        String tokenHash = opaqueTokenService.hash(secret);

        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(EXPIRES_IN_MINUTES);

        EmailVerificationToken token = new EmailVerificationToken();

        token.setId(tokenId);
        token.setAccountId(accountId);
        token.setTokenHash(tokenHash);
        token.setExpiresAt(expiresAt);

        repository.save(token);

        String rawToken = opaqueTokenService.build(
                tokenId,
                secret
        );

        return new CreatedVerificationToken(
                rawToken,
                expiresAt
        );
    }

    public record CreatedVerificationToken(
            String token,
            LocalDateTime expiresAt
    ) {
    }
}