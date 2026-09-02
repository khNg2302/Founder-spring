package founder_spring.authorization.service;

import founder_spring.authorization.dto.CreateRoleRequest;
import founder_spring.authorization.dto.RoleResponse;
import founder_spring.authorization.dto.UpdateRoleRequest;
import founder_spring.authorization.entity.Role;
import founder_spring.authorization.repository.RolePermissionRepository;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.authorization.repository.UserRoleRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.common.util.CuidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final CuidGenerator cuidGenerator;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            CuidGenerator cuidGenerator, UserRoleRepository userRoleRepository, RolePermissionRepository rolePermissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.cuidGenerator = cuidGenerator;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public RoleResponse create(CreateRoleRequest request) {

        String name = request.name().trim();

        if (roleRepository.findByName(name).isPresent()) {
            throw new ConflictException(
                    "Role already exists"
            );
        }

        Role role = new Role();
        role.setId(cuidGenerator.generate());
        role.setName(name);
        role.setDescription(request.description());

        Role savedRole = roleRepository.save(role);

        return new RoleResponse(
                savedRole.getId(),
                savedRole.getName(),
                savedRole.getDescription()
        );
    }

    @Transactional
    public RoleResponse update(
            String roleId,
            UpdateRoleRequest request
    ) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found")
                );

        String name = request.name().trim();

        roleRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(roleId))
                .ifPresent(existing -> {
                    throw new ConflictException("Role already exists");
                });

        role.setName(name);
        role.setDescription(request.description());

        Role savedRole = roleRepository.save(role);

        return new RoleResponse(
                savedRole.getId(),
                savedRole.getName(),
                savedRole.getDescription()
        );
    }

    @Transactional
    public void delete(String roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found")
                );

        if (userRoleRepository.existsByIdRoleId(roleId)) {
            throw new ConflictException(
                    "Cannot delete role because it is assigned to users"
            );
        }

        if (rolePermissionRepository.existsByIdRoleId(roleId)) {
            throw new ConflictException(
                    "Cannot delete role because it has permissions"
            );
        }

        roleRepository.delete(role);
    }
}