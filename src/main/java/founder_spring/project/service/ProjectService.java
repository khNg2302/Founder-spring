package founder_spring.project.service;


import founder_spring.category.dto.CategorySummaryResponse;
import founder_spring.category.entity.Category;
import founder_spring.category.exception.CategoryNotFoundException;
import founder_spring.category.repository.CategoryRepository;
import founder_spring.common.util.CuidGenerator;
import founder_spring.project.dto.CreateProjectRequest;
import founder_spring.project.dto.ProjectResponse;
import founder_spring.project.dto.UpdateProjectRequest;
import founder_spring.project.entity.Project;
import founder_spring.project.exception.ProjectNotFoundException;
import founder_spring.project.repository.ProjectRepository;
import founder_spring.project_category.entity.ProjectCategory;
import founder_spring.project_category.repository.ProjectCategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CuidGenerator cuidGenerator;
    private final ProjectCategoryRepository projectCategoryRepository;
    private final CategoryRepository categoryRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            CuidGenerator cuidGenerator, ProjectCategoryRepository projectCategoryRepository,CategoryRepository categoryRepository
    ) {
        this.projectRepository = projectRepository;
        this.cuidGenerator = cuidGenerator;
        this.projectCategoryRepository = projectCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    private ProjectResponse toResponse(Project project) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setScope(project.getScope());
        response.setStage(project.getStage());
        response.setActivityStatus(project.getActivityStatus());
        response.setDetailLocation(project.getDetailLocation());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        List<CategorySummaryResponse> categories =
                project.getProjectCategories()
                        .stream()
                        .map(projectCategory ->
                                new CategorySummaryResponse(
                                        projectCategory.getCategory().getId(),
                                        projectCategory.getCategory().getName(),
                                        projectCategory.getCategory().getType()
                                )
                        )
                        .toList();

        response.setCategories(categories);

        return response;
    }



    public Project findById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {

        Project project = new Project();

        project.setId(cuidGenerator.generate());
        project.setName(request.getName());
        project.setScope(request.getScope());
        project.setStage(request.getStage());
        project.setActivityStatus(request.getActivityStatus());
        project.setDetailLocation(request.getDetailLocation());

        if (request.getCategoryIds() != null
                && !request.getCategoryIds().isEmpty()) {

            for (String categoryId : request.getCategoryIds()) {

                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(categoryId)
                        );

                ProjectCategory projectCategory = new ProjectCategory();

                projectCategory.setProject(project);
                projectCategory.setCategory(category);

                project.getProjectCategories().add(projectCategory);
            }
        }

        Project savedProject = projectRepository.save(project);

        return toResponse(savedProject);
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

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {

        return projectRepository.findAllWithCategories()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(String id) {

        Project project = projectRepository.findByIdWithCategories(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id));

        return toResponse(project);
    }
}