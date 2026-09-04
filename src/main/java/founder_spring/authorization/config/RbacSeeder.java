package founder_spring.authorization.config;

import founder_spring.account.entity.Account;
import founder_spring.account.repository.AccountRepository;
import founder_spring.authorization.entity.*;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.repository.RolePermissionRepository;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.authorization.repository.UserRoleRepository;
import founder_spring.common.util.CuidGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RbacSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final CuidGenerator cuidGenerator;
    private final AccountRepository accountRepository;
    private final UserRoleRepository userRoleRepository;

    @Value("${admin.email:}")
    private String adminEmail;

    public RbacSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            CuidGenerator cuidGenerator, AccountRepository accountRepository, UserRoleRepository userRoleRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.cuidGenerator = cuidGenerator;
        this.accountRepository = accountRepository;
        this.userRoleRepository = userRoleRepository;
    }


    @PostConstruct
    @Transactional
    public void seed() {
        seedPermissions();
        seedAdminRole();
        seedAdminUser();
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
                RbacPermission.PERMISSION_DELETE,

                RbacPermission.AUDIENCE_TYPE_READ,
                RbacPermission.AUDIENCE_TYPE_CREATE,
                RbacPermission.AUDIENCE_TYPE_UPDATE,
                RbacPermission.AUDIENCE_TYPE_DELETE
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

    private void seedAdminUser() {

        if (adminEmail == null || adminEmail.isBlank()) {
            return;
        }

        List<Account> accounts =
                accountRepository.findAllByEmailIgnoreCase(
                        adminEmail.trim()
                );

        if (accounts.isEmpty()) {
            throw new IllegalStateException(
                    "Admin account not found: " + adminEmail
            );
        }

        Set<String> userIds = accounts.stream()
                .map(Account::getUserId)
                .collect(Collectors.toSet());

        if (userIds.size() != 1) {
            throw new IllegalStateException(
                    "Admin email belongs to multiple users: "
                            + adminEmail
            );
        }

        String userId = userIds.iterator().next();

        Role adminRole = roleRepository
                .findByName(RbacRole.ADMIN)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ADMIN role not found"
                        )
                );

        UserRoleId id = new UserRoleId(
                userId,
                adminRole.getId()
        );

        if (userRoleRepository.existsById(id)) {
            return;
        }

        UserRole userRole = new UserRole();
        userRole.setId(id);

        userRoleRepository.save(userRole);
    }
}