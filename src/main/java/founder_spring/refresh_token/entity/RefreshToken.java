package founder_spring.refresh_token.entity;

import founder_spring.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "\"RefreshToken\"",
        indexes = {
                @Index(
                        name = "RefreshToken_userId_idx",
                        columnList = "userId"
                ),
                @Index(
                        name = "RefreshToken_accountId_idx",
                        columnList = "accountId"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "\"userId\"", nullable = false, length = 25)
    private String userId;

    @Column(name = "\"accountId\"", nullable = false, length = 25)
    private String accountId;

    @Column(name = "\"tokenHash\"", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "\"expiresAt\"", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "\"revokedAt\"")
    private LocalDateTime revokedAt;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "\"userId\"",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}