package founder_spring.authorization.config;

import founder_spring.authorization.entity.Permission;
import founder_spring.authorization.entity.Role;
import founder_spring.authorization.entity.RolePermission;
import founder_spring.authorization.entity.RolePermissionId;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.repository.RolePermissionRepository;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.common.util.CuidGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RbacSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CuidGenerator cuidGenerator;

    public RbacSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            CuidGenerator cuidGenerator
    ) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.cuidGenerator = cuidGenerator;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        seedPermissions();
        seedAdminRole();
    }

    private void seedPermissions() {

        List<String> permissions = List.of(
                RbacPermission.USER_READ,
                RbacPermission.USER_CREATE,
                RbacPermission.USER_UPDATE,
                RbacPermission.USER_DELETE,

                RbacPermission.USER_ROLE_ASSIGN,
                RbacPermission.USER_ROLE_REMOVE,

                RbacPermission.ROLE_READ,
                RbacPermission.ROLE_CREATE,
                RbacPermission.ROLE_UPDATE,
                RbacPermission.ROLE_DELETE,

                RbacPermission.ROLE_PERMISSION_ASSIGN,
                RbacPermission.ROLE_PERMISSION_REMOVE,

                RbacPermission.PERMISSION_READ,
                RbacPermission.PERMISSION_CREATE,
                RbacPermission.PERMISSION_UPDATE,
                RbacPermission.PERMISSION_DELETE
        );

        for (String name : permissions) {

            if (permissionRepository.findByName(name).isEmpty()) {

                Permission permission = new Permission();

                permission.setId(cuidGenerator.generate());
                permission.setName(name);

                permissionRepository.save(permission);
            }
        }
    }

    private void seedAdminRole() {

        Role adminRole = roleRepository
                .findByName(RbacRole.ADMIN)
                .orElseGet(() -> {

                    Role role = new Role();

                    role.setId(cuidGenerator.generate());
                    role.setName(RbacRole.ADMIN);
                    role.setDescription(
                            "System administrator"
                    );

                    return roleRepository.save(role);
                });

        List<Permission> permissions =
                permissionRepository.findAll();

        for (Permission permission : permissions) {

            RolePermissionId id =
                    new RolePermissionId(
                            adminRole.getId(),
                            permission.getId()
                    );

            if (!rolePermissionRepository.existsById(id)) {

                RolePermission rolePermission =
                        new RolePermission();

                rolePermission.setId(id);

                rolePermissionRepository.save(
                        rolePermission
                );
            }
        }
    }
}