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
public class UserRoleId implements Serializable {

    @Column(name = "\"userId\"", length = 25)
    private String userId;

    @Column(name = "\"roleId\"", length = 25)
    private String roleId;
}
