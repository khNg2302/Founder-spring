package founder_spring.user.controller;

import founder_spring.common.dto.PageResponse;
import founder_spring.user.dto.UpdateUserStatusRequest;
import founder_spring.user.dto.UserDetailResponse;
import founder_spring.user.dto.UserListResponse;
import founder_spring.user.entity.UserStatus;
import founder_spring.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public PageResponse<UserListResponse> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return userService.getUsers(
                search,
                status,
                pageable
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public UserDetailResponse getUserById(
            @PathVariable String userId
    ) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('user:update')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            Authentication authentication
    ) {
        userService.updateUserStatus(
                userId,
                request.status(),
                authentication.getName()
        );
    }
}