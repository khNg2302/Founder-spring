package founder_spring.authorization.service;

import founder_spring.authorization.dto.RolePermissionResponse;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.repository.RolePermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionService(
            RolePermissionRepository rolePermissionRepository,
            PermissionRepository permissionRepository
    ) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RolePermissionResponse> getPermissionsByRoleId(
            String roleId
    ) {
        return rolePermissionRepository.findByIdRoleId(roleId)
                .stream()
                .map(rolePermission ->
                        permissionRepository.findById(
                                rolePermission.getId().getPermissionId()
                        ).orElse(null)
                )
                .filter(Objects::nonNull)
                .map(permission ->
                        new RolePermissionResponse(
                                permission.getId(),
                                permission.getName()
                        )
                )
                .toList();
    }
}