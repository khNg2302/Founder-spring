package founder_spring.project.dto;

import founder_spring.project.entity.ProjectActivityStatus;
import founder_spring.project.entity.ProjectScope;
import founder_spring.project.entity.ProjectStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 100, message = "Project name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Project scope is required")
    private ProjectScope scope;

    @NotNull(message = "Project stage is required")
    private ProjectStage stage;

    @NotNull(message = "Project activity status is required")
    private ProjectActivityStatus activityStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectScope getScope() {
        return scope;
    }

    public void setScope(ProjectScope scope) {
        this.scope = scope;
    }

    public ProjectStage getStage() {
        return stage;
    }

    public void setStage(ProjectStage stage) {
        this.stage = stage;
    }

    public ProjectActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ProjectActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}