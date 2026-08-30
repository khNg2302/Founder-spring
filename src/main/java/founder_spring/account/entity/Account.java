package founder_spring.account.entity;

import founder_spring.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "\"Account\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "Account_provider_providerAccountId_key",
                        columnNames = {"provider", "providerAccountId"}
                ),
                @UniqueConstraint(
                        name = "Account_provider_email_key",
                        columnNames = {"provider", "email"}
                )
        },
        indexes = {
                @Index(
                        name = "Account_userId_idx",
                        columnList = "userId"
                ),
                @Index(
                        name = "Account_email_idx",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "\"userId\"", nullable = false, length = 25)
    private String userId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "provider", nullable = false)
    private AccountProvider provider;

    @Column(name = "\"providerAccountId\"")
    private String providerAccountId;

    @Column(name = "email")
    private String email;

    @Column(name = "\"passwordHash\"")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "\"deletedAt\"")
    private LocalDateTime deletedAt;

    @Column(name = "\"emailVerifiedAt\"")
    private LocalDateTime emailVerifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "\"userId\"",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private User user;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
