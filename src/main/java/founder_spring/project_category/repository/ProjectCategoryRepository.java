package founder_spring.project_category.repository;

import founder_spring.project_category.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCategoryRepository
        extends JpaRepository<ProjectCategory, Long> {
}