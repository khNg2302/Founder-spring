package founder_spring.project_audience.controller;

import founder_spring.project_audience.dto.AudienceTypeResponse;
import founder_spring.project_audience.dto.CreateAudienceTypeRequest;
import founder_spring.project_audience.dto.UpdateAudienceTypeRequest;
import founder_spring.project_audience.service.AudienceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/audience-types")
@RequiredArgsConstructor
public class AdminAudienceTypeController {

    private final AudienceTypeService audienceTypeService;

    @PreAuthorize("hasAuthority('audience_type:read')")
    @GetMapping
    public ResponseEntity<List<AudienceTypeResponse>> getAll() {
        return ResponseEntity.ok(audienceTypeService.getAll());
    }

    @PreAuthorize("hasAuthority('audience_type:read')")
    @GetMapping("/{id}")
    public ResponseEntity<AudienceTypeResponse> getById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(audienceTypeService.getById(id));
    }

    @PreAuthorize("hasAuthority('audience_type:create')")
    @PostMapping
    public ResponseEntity<AudienceTypeResponse> create(
            @Valid @RequestBody CreateAudienceTypeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(audienceTypeService.create(request));
    }

    @PreAuthorize("hasAuthority('audience_type:update')")
    @PutMapping("/{id}")
    public ResponseEntity<AudienceTypeResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateAudienceTypeRequest request
    ) {
        return ResponseEntity.ok(
                audienceTypeService.update(id, request)
        );
    }

    @PreAuthorize("hasAuthority('audience_type:update')")
    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> updateActive(
            @PathVariable String id,
            @RequestParam boolean active
    ) {
        audienceTypeService.updateActive(id, active);
        return ResponseEntity.noContent().build();
    }
}