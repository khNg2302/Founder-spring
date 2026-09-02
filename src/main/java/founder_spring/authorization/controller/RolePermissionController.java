package founder_spring.authorization.controller;

import founder_spring.authorization.dto.RolePermissionResponse;
import founder_spring.authorization.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('role:permission:assign')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignPermission(
            @PathVariable String roleId,
            @PathVariable String permissionId
    ) {
        rolePermissionService.assignPermission(
                roleId,
                permissionId
        );
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('role:permission:remove')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePermission(
            @PathVariable String roleId,
            @PathVariable String permissionId
    ) {
        rolePermissionService.removePermission(
                roleId,
                permissionId
        );
    }
}