package founder_spring.user.repository;

import founder_spring.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, String> {
}