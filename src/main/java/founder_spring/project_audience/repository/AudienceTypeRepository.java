package founder_spring.project_audience.repository;

import founder_spring.project_audience.entity.AudienceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AudienceTypeRepository
        extends JpaRepository<AudienceType, String> {

    Optional<AudienceType> findByNameIgnoreCase(String name);

    List<AudienceType> findAllByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}