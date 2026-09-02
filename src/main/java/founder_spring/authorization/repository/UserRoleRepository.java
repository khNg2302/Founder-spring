package founder_spring.authorization.repository;

import founder_spring.authorization.entity.UserRole;
import founder_spring.authorization.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByIdUserId(String userId);

    boolean existsByIdRoleId(String roleId);
}