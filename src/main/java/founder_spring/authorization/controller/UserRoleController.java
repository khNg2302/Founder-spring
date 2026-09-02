package founder_spring.authorization.controller;

import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.authorization.service.UserRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:read')")
    public List<UserRoleResponse> getUserRoles(
            @PathVariable String userId
    ) {
        return userRoleService.getRolesByUserId(userId);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('user:role:assign')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignRole(
            @PathVariable String userId,
            @PathVariable String roleId
    ) {
        userRoleService.assignRole(userId, roleId);
    }
}