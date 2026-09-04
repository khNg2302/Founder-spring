package founder_spring.project_audience.controller;

import founder_spring.project_audience.dto.AudienceTypeResponse;
import founder_spring.project_audience.service.AudienceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audience-types")
@RequiredArgsConstructor
public class AudienceTypeController {

    private final AudienceTypeService audienceTypeService;

    @GetMapping
    public ResponseEntity<List<AudienceTypeResponse>> getActiveAudienceTypes() {
        return ResponseEntity.ok(
                audienceTypeService.getActiveAudienceTypes()
        );
    }
}