package founder_spring.user.dto;

import founder_spring.account.entity.AccountProvider;
import founder_spring.account.entity.AccountStatus;
import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserDetailResponse(
        String id,
        String name,
        String avatarUrl,
        UserStatus status,
        LocalDateTime deletionRequestedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AccountResponse> accounts,
        List<UserRoleResponse> roles
) {

    public record AccountResponse(
            AccountProvider provider,
            String email,
            AccountStatus status,
            LocalDateTime emailVerifiedAt
    ) {}
}