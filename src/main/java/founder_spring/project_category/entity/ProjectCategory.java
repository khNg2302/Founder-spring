package founder_spring.project_category.entity;

import founder_spring.category.entity.Category;
import founder_spring.project.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "project_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_category",
                        columnNames = {"project_id", "category_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ProjectCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}