package founder_spring.project.repository;

import founder_spring.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, String> {
    @Query("""
    SELECT DISTINCT p
    FROM Project p
    LEFT JOIN FETCH p.projectCategories pc
    LEFT JOIN FETCH pc.category
""")
    List<Project> findAllWithCategories();
    @Query("""
    SELECT DISTINCT p
    FROM Project p
    LEFT JOIN FETCH p.projectCategories pc
    LEFT JOIN FETCH pc.category
    WHERE p.id = :id
""")
    Optional<Project> findByIdWithCategories(@Param("id") String id);
}