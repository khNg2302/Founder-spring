package founder_spring.project.repository;

import founder_spring.project.entity.Project;
import founder_spring.project.entity.ProjectActivityStatus;
import founder_spring.project.entity.ProjectScope;
import founder_spring.project.entity.ProjectStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, String> {

    @Query(
            value = """
        SELECT p
        FROM Project p
        WHERE (:scope IS NULL OR p.scope = :scope)
          AND (:stage IS NULL OR p.stage = :stage)
          AND (:activityStatus IS NULL OR p.activityStatus = :activityStatus)
          AND (
              :categoryId IS NULL
              OR EXISTS (
                  SELECT 1
                  FROM ProjectCategory pc
                  WHERE pc.project = p
                    AND pc.category.id = :categoryId
              )
          )
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM Project p
        WHERE (:scope IS NULL OR p.scope = :scope)
          AND (:stage IS NULL OR p.stage = :stage)
          AND (:activityStatus IS NULL OR p.activityStatus = :activityStatus)
          AND (
              :categoryId IS NULL
              OR EXISTS (
                  SELECT 1
                  FROM ProjectCategory pc
                  WHERE pc.project = p
                    AND pc.category.id = :categoryId
              )
          )
        """
    )
    Page<Project> findAllProjects(
            @Param("scope") ProjectScope scope,
            @Param("stage") ProjectStage stage,
            @Param("activityStatus") ProjectActivityStatus activityStatus,
            @Param("categoryId") String categoryId,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT p
        FROM Project p
        LEFT JOIN FETCH p.projectCategories pc
        LEFT JOIN FETCH pc.category
        WHERE p.id IN :ids
        """)
    List<Project> findAllWithCategoriesByIds(
            @Param("ids") List<String> ids
    );

    @Query("""
        SELECT DISTINCT p
        FROM Project p
        LEFT JOIN FETCH p.projectCategories pc
        LEFT JOIN FETCH pc.category
        WHERE p.id = :id
        """)
    Optional<Project> findByIdWithCategories(
            @Param("id") String id
    );
}