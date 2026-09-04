package founder_spring.project.dto;

import founder_spring.project.entity.ProjectActivityStatus;
import founder_spring.project.entity.ProjectScope;
import founder_spring.project.entity.ProjectStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 100, message = "Project name must not exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Project scope is required")
    private ProjectScope scope;

    @NotNull(message = "Project stage is required")
    private ProjectStage stage;

    @NotNull(message = "Project activity status is required")
    private ProjectActivityStatus activityStatus;

    private List<String> categoryIds;

    @Size(max = 500, message = "Detail location must not exceed 500 characters")
    private String detailLocation;

    private List<String> audienceTypeIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<String> getAudienceTypeIds() {
        return audienceTypeIds;
    }

    public void setAudienceTypeIds(List<String> audienceTypeIds) {
        this.audienceTypeIds = audienceTypeIds;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }


}