package founder_spring.email_verification_token.repository;

import founder_spring.email_verification_token.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, String> {
}