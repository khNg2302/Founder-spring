package founder_spring.project_audience.entity;

import founder_spring.project.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "project_audiences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_audience",
                        columnNames = {"project_id", "audience_type_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ProjectAudience {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "audience_type_id", nullable = false)
    private AudienceType audienceType;
}