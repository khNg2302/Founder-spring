package founder_spring.project.dto;

import founder_spring.category.dto.CategorySummaryResponse;
import founder_spring.project.entity.ProjectActivityStatus;
import founder_spring.project.entity.ProjectScope;
import founder_spring.project.entity.ProjectStage;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectResponse {

    private String id;

    private String name;

    private ProjectScope scope;

    private ProjectStage stage;

    private ProjectActivityStatus activityStatus;

    private String detailLocation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CategorySummaryResponse> categories;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CategorySummaryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategorySummaryResponse> categories) {
        this.categories = categories;
    }
}