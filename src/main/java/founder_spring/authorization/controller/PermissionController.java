package founder_spring.authorization.controller;

import founder_spring.authorization.dto.CreatePermissionRequest;
import founder_spring.authorization.dto.PermissionResponse;
import founder_spring.authorization.dto.UpdatePermissionRequest;
import founder_spring.authorization.repository.PermissionRepository;
import founder_spring.authorization.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;

    public PermissionController(
            PermissionRepository permissionRepository, PermissionService permissionService
    ) {
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public List<PermissionResponse> getPermissions() {

        return permissionRepository.findAll()
                .stream()
                .map(permission -> new PermissionResponse(
                        permission.getId(),
                        permission.getName(),
                        permission.getDescription()
                ))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request
    ) {
        PermissionResponse response = permissionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('permission:update')")
    public PermissionResponse updatePermission(
            @PathVariable String permissionId,
            @Valid @RequestBody UpdatePermissionRequest request
    ) {
        return permissionService.update(permissionId, request);
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('permission:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(
            @PathVariable String permissionId
    ) {
        permissionService.delete(permissionId);
    }
}