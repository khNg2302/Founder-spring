package founder_spring.project.service;


import founder_spring.common.util.CuidGenerator;
import founder_spring.project.dto.CreateProjectRequest;
import founder_spring.project.dto.UpdateProjectRequest;
import founder_spring.project.entity.Project;
import founder_spring.project.exception.ProjectNotFoundException;
import founder_spring.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CuidGenerator cuidGenerator;

    public ProjectService(
            ProjectRepository projectRepository,
            CuidGenerator cuidGenerator
    ) {
        this.projectRepository = projectRepository;
        this.cuidGenerator = cuidGenerator;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public Project create(CreateProjectRequest request) {

        Project project = new Project();

        project.setId(cuidGenerator.generate());
        project.setName(request.getName());
        project.setScope(request.getScope());
        project.setStage(request.getStage());
        project.setActivityStatus(request.getActivityStatus());

        return projectRepository.save(project);
    }

    public void delete(String id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        projectRepository.delete(project);
    }

    public Project update(String id, UpdateProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        if (request.getName() != null) {
            project.setName(request.getName());
        }

        if (request.getScope() != null) {
            project.setScope(request.getScope());
        }

        if (request.getStage() != null) {
            project.setStage(request.getStage());
        }

        if (request.getActivityStatus() != null) {
            project.setActivityStatus(request.getActivityStatus());
        }

        if (request.getDetailLocation() != null) {
            project.setDetailLocation(request.getDetailLocation());
        }

        return projectRepository.save(project);
    }
}