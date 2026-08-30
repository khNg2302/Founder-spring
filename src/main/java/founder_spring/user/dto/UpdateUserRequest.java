package founder_spring.user.dto;

import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(
            max = 100,
            message = "User name must not exceed 100 characters"
    )
    private String name;

    @Size(
            max = 500,
            message = "Avatar URL must not exceed 500 characters"
    )
    private String avatarUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}