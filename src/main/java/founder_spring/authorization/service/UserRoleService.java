package founder_spring.authorization.service;

import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.authorization.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserRoleResponse> getRolesByUserId(String userId) {

        return userRoleRepository.findByIdUserId(userId)
                .stream()
                .map(userRole -> roleRepository
                        .findById(userRole.getId().getRoleId())
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .map(role -> new UserRoleResponse(
                        role.getId(),
                        role.getName()
                ))
                .toList();
    }
}