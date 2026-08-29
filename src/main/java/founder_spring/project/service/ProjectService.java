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
import founder_spring.project.entity.ProjectActivityStatus;
import founder_spring.project.entity.ProjectScope;
import founder_spring.project.entity.ProjectStage;
import founder_spring.project.exception.ProjectNotFoundException;
import founder_spring.project.repository.ProjectRepository;
import founder_spring.project_category.entity.ProjectCategory;
import founder_spring.project_category.repository.ProjectCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CuidGenerator cuidGenerator;
    private final CategoryRepository categoryRepository;
    private final ProjectCategoryRepository projectCategoryRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            CuidGenerator cuidGenerator,
            CategoryRepository categoryRepository,
            ProjectCategoryRepository projectCategoryRepository
    ) {
        this.projectRepository = projectRepository;
        this.cuidGenerator = cuidGenerator;
        this.categoryRepository = categoryRepository;
        this.projectCategoryRepository = projectCategoryRepository;
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
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );
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

                project.getProjectCategories()
                        .add(projectCategory);
            }
        }

        Project savedProject =
                projectRepository.save(project);

        return toResponse(savedProject);
    }

    @Transactional
    public ProjectResponse update(
            String id,
            UpdateProjectRequest request
    ) {

        Project project = projectRepository
                .findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );

        project.setName(request.getName());
        project.setScope(request.getScope());
        project.setDetailLocation(request.getDetailLocation());
        project.setStage(request.getStage());
        project.setActivityStatus(request.getActivityStatus());

        projectRepository.save(project);

        projectCategoryRepository.deleteAllByProjectId(id);

        projectCategoryRepository.flush();

        if (request.getCategoryIds() != null) {

            for (String categoryId : request.getCategoryIds()) {

                Category category = categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(categoryId)
                        );

                ProjectCategory projectCategory =
                        new ProjectCategory();

                projectCategory.setProject(project);
                projectCategory.setCategory(category);

                projectCategoryRepository.save(projectCategory);
            }
        }

        projectCategoryRepository.flush();

        Project updatedProject = projectRepository
                .findByIdWithCategories(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );

        return toResponse(updatedProject);
    }

    @Transactional
    public void delete(String id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAll(
            Pageable pageable,
            ProjectScope scope,
            ProjectStage stage,
            ProjectActivityStatus activityStatus
    ) {

        Page<Project> projectPage =
                projectRepository.findAllProjects(
                        scope,
                        stage,
                        activityStatus,
                        pageable
                );

        List<String> projectIds =
                projectPage.getContent()
                        .stream()
                        .map(Project::getId)
                        .toList();

        if (projectIds.isEmpty()) {
            return projectPage.map(this::toResponse);
        }

        List<Project> projectsWithCategories =
                projectRepository.findAllWithCategoriesByIds(projectIds);

        Map<String, Project> projectMap =
                projectsWithCategories.stream()
                        .collect(Collectors.toMap(
                                Project::getId,
                                project -> project
                        ));

        return projectPage.map(project ->
                toResponse(projectMap.get(project.getId()))
        );
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(String id) {

        Project project = projectRepository
                .findByIdWithCategories(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );

        return toResponse(project);
    }
}