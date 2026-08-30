package founder_spring.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"User\"")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", length = 25)
    private String id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "\"avatarUrl\"", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "\"deletionRequestedAt\"")
    private LocalDateTime deletionRequestedAt;

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
