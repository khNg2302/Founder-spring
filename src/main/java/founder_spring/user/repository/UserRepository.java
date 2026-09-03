package founder_spring.user.repository;

import founder_spring.user.entity.User;
import founder_spring.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository
        extends JpaRepository<User, String> {
    @Query("""
    SELECT u
    FROM User u
    WHERE (
        LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR EXISTS (
            SELECT 1
            FROM Account a
            WHERE a.userId = u.id
              AND LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    )
    AND (
        :status IS NULL
        OR u.status = :status
    )
""")
    Page<User> findUsers(
            @Param("search") String search,
            @Param("status") UserStatus status,
            Pageable pageable
    );
}