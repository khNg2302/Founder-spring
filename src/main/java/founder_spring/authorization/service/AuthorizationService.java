package founder_spring.authorization.service;

import founder_spring.authorization.repository.PermissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AuthorizationService {

    private final PermissionRepository permissionRepository;

    public AuthorizationService(
            PermissionRepository permissionRepository
    ) {
        this.permissionRepository = permissionRepository;
    }

    public Set<String> getPermissions(String userId) {

        return permissionRepository
                .findPermissionNamesByUserId(userId);
    }
}