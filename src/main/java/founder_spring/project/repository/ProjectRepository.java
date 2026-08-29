package founder_spring.project.repository;

import founder_spring.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository
        extends JpaRepository<Project, String> {
}