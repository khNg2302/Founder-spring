package founder_spring.authorization.repository;

import founder_spring.authorization.entity.RolePermission;
import founder_spring.authorization.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findByIdRoleId(String roleId);

    boolean existsByIdRoleId(String roleId);
}