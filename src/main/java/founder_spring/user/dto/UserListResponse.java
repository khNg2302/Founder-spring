package founder_spring.user.dto;

import founder_spring.account.entity.AccountProvider;
import founder_spring.user.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserListResponse(
        String id,
        String name,
        String avatarUrl,
        String email,
        UserStatus status,
        List<AccountProvider> providers,
        LocalDateTime createdAt
) {}