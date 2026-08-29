package founder_spring.project_category.repository;

import founder_spring.project_category.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectCategoryRepository
        extends JpaRepository<ProjectCategory, Long> {

    @Modifying
    @Query("""
        DELETE FROM ProjectCategory pc
        WHERE pc.project.id = :projectId
    """)
    void deleteAllByProjectId(@Param("projectId") String projectId);
}