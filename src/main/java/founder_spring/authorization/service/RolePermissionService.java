package founder_spring.authorization.service;

import founder_spring.authorization.dto.RolePermissionResponse;
import founder_spring.authorization.entity.RolePermission;
import founder_spring.authorization.entity.RolePermissionId;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.repository.RolePermissionRepository;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public RolePermissionService(
            RolePermissionRepository rolePermissionRepository,
            PermissionRepository permissionRepository, RoleRepository roleRepository
    ) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
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

    @Transactional
    public void assignPermission(
            String roleId,
            String permissionId
    ) {
        roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found")
                );

        permissionRepository.findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Permission not found")
                );

        RolePermissionId id = new RolePermissionId(
                roleId,
                permissionId
        );

        if (rolePermissionRepository.existsById(id)) {
            throw new ConflictException(
                    "Permission is already assigned to this role"
            );
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(id);

        rolePermissionRepository.save(rolePermission);
    }

    @Transactional
    public void removePermission(
            String roleId,
            String permissionId
    ) {
        RolePermissionId id = new RolePermissionId(
                roleId,
                permissionId
        );

        if (!rolePermissionRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Permission is not assigned to this role"
            );
        }

        rolePermissionRepository.deleteById(id);
    }
}