package founder_spring.email_verification_token.entity;

import founder_spring.account.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "\"EmailVerificationToken\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "EmailVerificationToken_tokenHash_key",
                        columnNames = {"tokenHash"}
                )
        },
        indexes = {
                @Index(
                        name = "EmailVerificationToken_accountId_idx",
                        columnList = "accountId"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationToken {

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "accountId", nullable = false, length = 25)
    private String accountId;

    @Column(name = "tokenHash", nullable = false)
    private String tokenHash;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "usedAt")
    private LocalDateTime usedAt;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "accountId",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private Account account;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}