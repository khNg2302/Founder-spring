package founder_spring.authorization.service;

import founder_spring.authorization.dto.CreatePermissionRequest;
import founder_spring.authorization.dto.PermissionResponse;
import founder_spring.authorization.entity.Permission;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.util.CuidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final CuidGenerator cuidGenerator;

    public PermissionService(
            PermissionRepository permissionRepository,
            CuidGenerator cuidGenerator
    ) {
        this.permissionRepository = permissionRepository;
        this.cuidGenerator = cuidGenerator;
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
}