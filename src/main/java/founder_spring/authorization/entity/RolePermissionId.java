package founder_spring.authorization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {

    @Column(name = "\"roleId\"", length = 25)
    private String roleId;

    @Column(name = "\"permissionId\"", length = 25)
    private String permissionId;
}
