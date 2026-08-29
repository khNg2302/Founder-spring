package founder_spring.project.controller;

import founder_spring.project.dto.ProjectResponse;
import founder_spring.project.dto.UpdateProjectRequest;
import founder_spring.project.entity.Project;
import founder_spring.project.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import founder_spring.project.dto.CreateProjectRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        projectService.delete(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        return projectService.update(id, request);
    }

    @GetMapping
    public Page<ProjectResponse> getAll(
            Pageable pageable
    ) {
        return projectService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable String id) {
        return projectService.getById(id);
    }
}