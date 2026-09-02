package founder_spring.authorization.repository;

import founder_spring.authorization.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository
        extends JpaRepository<Permission, String> {

    Optional<Permission> findByName(String name);

    @Query("""
        SELECT DISTINCT p.name
        FROM UserRole ur
        JOIN RolePermission rp
            ON rp.id.roleId = ur.id.roleId
        JOIN Permission p
            ON p.id = rp.id.permissionId
        WHERE ur.id.userId = :userId
    """)
    Set<String> findPermissionNamesByUserId(
            @Param("userId") String userId
    );
}