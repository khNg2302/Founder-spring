package founder_spring.authorization.repository;

import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.authorization.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

    @Query("""
    SELECT new founder_spring.authorization.dto.UserRoleResponse(
        ur.id.roleId,
        r.name
    )
    FROM UserRole ur
    JOIN Role r
        ON r.id = ur.id.roleId
    WHERE ur.id.userId = :userId
""")
    List<UserRoleResponse> findRolesByUserId(
            @Param("userId") String userId
    );
}