package founder_spring.authorization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {

    @Column(name = "\"roleId\"", length = 25)
    private String roleId;

    @Column(name = "\"permissionId\"", length = 25)
    private String permissionId;
}
