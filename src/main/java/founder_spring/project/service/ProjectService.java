package founder_spring.project.service;

import founder_spring.category.dto.CategorySummaryResponse;
import founder_spring.category.entity.Category;
import founder_spring.category.exception.CategoryNotFoundException;
import founder_spring.category.repository.CategoryRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
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
import founder_spring.project_audience.dto.AudienceTypeResponse;
import founder_spring.project_audience.entity.AudienceType;
import founder_spring.project_audience.entity.ProjectAudience;
import founder_spring.project_audience.repository.AudienceTypeRepository;
import founder_spring.project_audience.repository.ProjectAudienceRepository;
import founder_spring.project_category.entity.ProjectCategory;
import founder_spring.project_category.repository.ProjectCategoryRepository;
import founder_spring.user.entity.User;
import founder_spring.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final ProjectAudienceRepository projectAudienceRepository;
    private final AudienceTypeRepository audienceTypeRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            CuidGenerator cuidGenerator,
            CategoryRepository categoryRepository,
            ProjectCategoryRepository projectCategoryRepository, UserRepository userRepository, ProjectAudienceRepository projectAudienceRepository, AudienceTypeRepository audienceTypeRepository
    ) {
        this.projectRepository = projectRepository;
        this.cuidGenerator = cuidGenerator;
        this.categoryRepository = categoryRepository;
        this.projectCategoryRepository = projectCategoryRepository;
        this.userRepository = userRepository;
        this.projectAudienceRepository = projectAudienceRepository;

        this.audienceTypeRepository = audienceTypeRepository;
    }

    private ProjectResponse toResponse(
            Project project,
            List<ProjectAudience> audiences
    ) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
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

        List<AudienceTypeResponse> audienceTypes =
                audiences.stream()
                        .map(projectAudience -> {

                            AudienceType audienceType =
                                    projectAudience.getAudienceType();

                            return AudienceTypeResponse.builder()
                                    .id(audienceType.getId())
                                    .name(audienceType.getName())
                                    .description(audienceType.getDescription())
                                    .active(audienceType.getActive())
                                    .createdAt(audienceType.getCreatedAt())
                                    .updatedAt(audienceType.getUpdatedAt())
                                    .build();
                        })
                        .toList();

        response.setAudienceTypes(audienceTypes);

        return response;
    }

    private ProjectResponse toResponse(Project project) {

        List<ProjectAudience> audiences =
                projectAudienceRepository
                        .findAllByProjectId(project.getId());

        return toResponse(project, audiences);
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {

        Project project = new Project();

        project.setId(cuidGenerator.generate());
        project.setCreatedBy(getCurrentUser());

        project.setName(request.getName());
        project.setDescription(request.getDescription());
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

        if (request.getAudienceTypeIds() != null
                && !request.getAudienceTypeIds().isEmpty()) {

            if (request.getAudienceTypeIds() != null
                    && request.getAudienceTypeIds().size()
                    != request.getAudienceTypeIds().stream().distinct().count()) {

                throw new ConflictException(
                        "Duplicate audience type"
                );
            }

            for (String audienceTypeId : request.getAudienceTypeIds()) {

                AudienceType audienceType = audienceTypeRepository
                        .findById(audienceTypeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audience type not found"
                                )
                        );

                if (!audienceType.getActive()) {
                    throw new ConflictException(
                            "Audience type is inactive: " + audienceTypeId
                    );
                }

                ProjectAudience projectAudience = new ProjectAudience();

                projectAudience.setId(cuidGenerator.generate());
                projectAudience.setProject(project);
                projectAudience.setAudienceType(audienceType);

                project.getProjectAudiences()
                        .add(projectAudience);
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

        verifyOwnership(project);

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setScope(request.getScope());
        project.setDetailLocation(request.getDetailLocation());
        project.setStage(request.getStage());
        project.setActivityStatus(request.getActivityStatus());

        projectCategoryRepository.deleteAllByProjectId(id);

        projectCategoryRepository.flush();

        projectAudienceRepository.deleteAllByProjectId(id);

        projectAudienceRepository.flush();

        if (request.getAudienceTypeIds() != null) {

            if (request.getAudienceTypeIds().size()
                    != request.getAudienceTypeIds()
                    .stream()
                    .distinct()
                    .count()) {

                throw new ConflictException(
                        "Duplicate audience type"
                );
            }

            for (String audienceTypeId : request.getAudienceTypeIds()) {

                AudienceType audienceType = audienceTypeRepository
                        .findById(audienceTypeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Audience type not found"
                                )
                        );

                if (!audienceType.getActive()) {
                    throw new ConflictException(
                            "Audience type is inactive"
                    );
                }

                ProjectAudience projectAudience =
                        new ProjectAudience();

                projectAudience.setId(cuidGenerator.generate());
                projectAudience.setProject(project);
                projectAudience.setAudienceType(audienceType);

                projectAudienceRepository.save(projectAudience);
            }
        }

        projectAudienceRepository.flush();

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

        Project project = projectRepository
                .findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(id)
                );

        verifyOwnership(project);

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAll(
            Pageable pageable,
            ProjectScope scope,
            ProjectStage stage,
            ProjectActivityStatus activityStatus,
            String categoryId
    ) {

        Page<Project> projectPage =
                projectRepository.findAllProjects(
                        scope,
                        stage,
                        activityStatus,
                        categoryId,
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

        List<ProjectAudience> projectAudiences =
                projectAudienceRepository
                        .findAllByProjectIdIn(projectIds);

        Map<String, List<ProjectAudience>> audienceMap =
                projectAudiences.stream()
                        .collect(Collectors.groupingBy(
                                projectAudience ->
                                        projectAudience.getProject().getId()
                        ));

        Map<String, Project> projectMap =
                projectsWithCategories.stream()
                        .collect(Collectors.toMap(
                                Project::getId,
                                project -> project
                        ));

        return projectPage.map(project -> {

            Project projectWithCategories =
                    projectMap.get(project.getId());

            List<ProjectAudience> audiences =
                    audienceMap.getOrDefault(
                            project.getId(),
                            List.of()
                    );

            return toResponse(
                    projectWithCategories,
                    audiences
            );
        });
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

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String userId = authentication.getName();

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    private void verifyOwnership(Project project) {

        User currentUser = getCurrentUser();

        if (!project.getCreatedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new AuthorizationDeniedException(
                    "You do not own this project"
            );
        }
    }
}