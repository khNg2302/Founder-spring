package founder_spring.authorization.repository;

import founder_spring.authorization.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);
}