package founder_spring.authorization.service;

import founder_spring.authorization.dto.CreatePermissionRequest;
import founder_spring.authorization.dto.PermissionResponse;
import founder_spring.authorization.dto.UpdatePermissionRequest;
import founder_spring.authorization.entity.Permission;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.repository.RolePermissionRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.common.util.CuidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final CuidGenerator cuidGenerator;
    private final RolePermissionRepository rolePermissionRepository;

    public PermissionService(
            PermissionRepository permissionRepository,
            CuidGenerator cuidGenerator, RolePermissionRepository rolePermissionRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.cuidGenerator = cuidGenerator;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public PermissionResponse create(CreatePermissionRequest request) {

        String name = request.name().trim();

        if (permissionRepository.findByName(name).isPresent()) {
            throw new ConflictException(
                    "Permission with name '" + name + "' already exists"
            );
        }

        Permission permission = new Permission();

        permission.setId(cuidGenerator.generate());
        permission.setName(name);
        permission.setDescription(
                request.description() != null
                        ? request.description().trim()
                        : null
        );

        Permission saved = permissionRepository.save(permission);

        return new PermissionResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );
    }

    public PermissionResponse update(
            String permissionId,
            UpdatePermissionRequest request
    ) {
        Permission permission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Permission not found"
                        )
                );

        String name = request.name().trim();

        permissionRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(permissionId))
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Permission with name '" + name + "' already exists"
                    );
                });

        permission.setName(name);

        permission.setDescription(
                request.description() != null
                        ? request.description().trim()
                        : null
        );

        Permission saved = permissionRepository.save(permission);

        return new PermissionResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );
    }

    public void delete(String permissionId) {

        Permission permission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Permission not found"
                        )
                );

        if (rolePermissionRepository.existsByIdPermissionId(permissionId)) {
            throw new ConflictException(
                    "Cannot delete permission because it is assigned to roles"
            );
        }

        permissionRepository.delete(permission);
    }
}