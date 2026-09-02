package founder_spring.authorization.controller;

import founder_spring.authorization.dto.CreateRoleRequest;
import founder_spring.authorization.dto.RoleResponse;
import founder_spring.authorization.dto.UpdateRoleRequest;
import founder_spring.authorization.entity.Role;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.authorization.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
public class RoleController {

    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public RoleController(RoleRepository roleRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public List<RoleResponse> getRoles() {

        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getName(),
                        role.getDescription()
                ))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        RoleResponse response =
                roleService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable String roleId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ResponseEntity.ok(
                roleService.update(roleId, request)
        );
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String roleId
    ) {
        roleService.delete(roleId);

        return ResponseEntity.noContent().build();
    }
}