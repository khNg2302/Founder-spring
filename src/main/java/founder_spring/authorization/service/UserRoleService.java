package founder_spring.authorization.service;

import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.authorization.entity.UserRole;
import founder_spring.authorization.entity.UserRoleId;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.authorization.repository.UserRoleRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public UserRoleService(
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository, UserRepository userRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserRoleResponse> getRolesByUserId(String userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    @Transactional
    public void assignRole(
            String userId,
            String roleId
    ) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found"
                        )
                );

        UserRoleId id = new UserRoleId(
                userId,
                roleId
        );

        if (userRoleRepository.existsById(id)) {
            throw new ConflictException(
                    "Role is already assigned to this user"
            );
        }

        UserRole userRole = new UserRole();
        userRole.setId(id);

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeRole(
            String userId,
            String roleId
    ) {
        UserRoleId id = new UserRoleId(
                userId,
                roleId
        );

        if (!userRoleRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Role is not assigned to this user"
            );
        }

        userRoleRepository.deleteById(id);
    }
}