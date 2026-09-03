package founder_spring.refresh_token.repository;

import founder_spring.refresh_token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
    UPDATE RefreshToken rt
    SET rt.revokedAt = CURRENT_TIMESTAMP
    WHERE rt.userId = :userId
      AND rt.revokedAt IS NULL
""")
    int revokeAllByUserId(@Param("userId") String userId);
}