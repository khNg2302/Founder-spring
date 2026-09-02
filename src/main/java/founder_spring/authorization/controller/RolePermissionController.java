package founder_spring.authorization.controller;

import founder_spring.authorization.dto.RolePermissionResponse;
import founder_spring.authorization.service.RolePermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(
            RolePermissionService rolePermissionService
    ) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:read')")
    public List<RolePermissionResponse> getRolePermissions(
            @PathVariable String roleId
    ) {
        return rolePermissionService.getPermissionsByRoleId(roleId);
    }
}