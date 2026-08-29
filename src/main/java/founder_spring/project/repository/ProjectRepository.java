package founder_spring.project.repository;

import founder_spring.project.entity.Project;
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
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Project p
            """
    )
    Page<Project> findAllProjects(Pageable pageable);

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