package founder_spring.project_audience.repository;

import founder_spring.project_audience.entity.ProjectAudience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProjectAudienceRepository
        extends JpaRepository<ProjectAudience, String> {

    List<ProjectAudience> findAllByProjectId(String projectId);

    boolean existsByProjectIdAndAudienceTypeId(
            String projectId,
            String audienceTypeId
    );

    void deleteAllByProjectId(String projectId);

    List<ProjectAudience> findAllByProjectIdIn(Collection<String> projectIds);
}