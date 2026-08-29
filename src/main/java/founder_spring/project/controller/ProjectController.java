package founder_spring.project.controller;

import founder_spring.project.dto.UpdateProjectRequest;
import founder_spring.project.entity.Project;
import founder_spring.project.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import founder_spring.project.dto.CreateProjectRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public Project findById(@PathVariable String id) {
        return projectService.findById(id);
    }

    @PostMapping
    public Project create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        projectService.delete(id);
    }

    @PatchMapping("/{id}")
    public Project update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProjectRequest request
    ) {
        return projectService.update(id, request);
    }
}