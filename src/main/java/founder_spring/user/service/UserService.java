package founder_spring.user.service;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import founder_spring.account.repository.AccountRepository;
import founder_spring.authorization.dto.UserRoleResponse;
import founder_spring.authorization.repository.RoleRepository;
import founder_spring.common.dto.PageResponse;
import founder_spring.common.exception.BadRequestException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.refresh_token.repository.RefreshTokenRepository;
import founder_spring.user.dto.UpdateUserRequest;
import founder_spring.user.dto.UserDetailResponse;
import founder_spring.user.dto.UserListResponse;
import founder_spring.user.dto.UserResponse;
import founder_spring.user.entity.User;
import founder_spring.user.entity.UserStatus;
import founder_spring.user.exception.UserNotFoundException;
import founder_spring.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private User findByIdOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setStatus(user.getStatus());
        response.setDeletionRequestedAt(
                user.getDeletionRequestedAt()
        );
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(String id) {

        return toResponse(
                findByIdOrThrow(id)
        );
    }

    @Transactional
    public UserResponse update(
            String id,
            UpdateUserRequest request
    ) {

        User user = findByIdOrThrow(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        return toResponse(
                userRepository.save(user)
        );
    }

    @Transactional
    public void delete(String id) {

        User user = findByIdOrThrow(id);

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserListResponse> getUsers(
            String search,
            UserStatus status,
            Pageable pageable
    ) {
        String normalizedSearch =
                search == null || search.isBlank()
                        ? ""
                        : search.trim();

        Page<User> users =
                userRepository.findUsers(
                        normalizedSearch,
                        status,
                        pageable
                );

        List<String> userIds = users.getContent()
                .stream()
                .map(User::getId)
                .toList();

        if (userIds.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    users.getNumber(),
                    users.getSize(),
                    users.getTotalElements(),
                    users.getTotalPages()
            );
        }

        List<Account> accounts =
                accountRepository.findAllByUserIdIn(userIds);

        Map<String, List<Account>> accountsByUserId =
                accounts.stream()
                        .collect(Collectors.groupingBy(Account::getUserId));

        List<UserListResponse> content = users.getContent()
                .stream()
                .map(user -> {
                    List<Account> userAccounts =
                            accountsByUserId.getOrDefault(
                                    user.getId(),
                                    List.of()
                            );

                    String email = userAccounts.stream()
                            .map(Account::getEmail)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                    List<AccountProvider> providers =
                            userAccounts.stream()
                                    .map(Account::getProvider)
                                    .distinct()
                                    .toList();

                    return new UserListResponse(
                            user.getId(),
                            user.getName(),
                            user.getAvatarUrl(),
                            email,
                            user.getStatus(),
                            providers,
                            user.getCreatedAt()
                    );
                })
                .toList();

        return new PageResponse<>(
                content,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        List<Account> accounts =
                accountRepository.findAllByUserId(userId);

        List<UserRoleResponse> roles =
                roleRepository.findRolesByUserId(userId);

        List<UserDetailResponse.AccountResponse> accountResponses =
                accounts.stream()
                        .map(account ->
                                new UserDetailResponse.AccountResponse(
                                        account.getProvider(),
                                        account.getEmail(),
                                        account.getStatus(),
                                        account.getEmailVerifiedAt()
                                )
                        )
                        .toList();

        return new UserDetailResponse(
                user.getId(),
                user.getName(),
                user.getAvatarUrl(),
                user.getStatus(),
                user.getDeletionRequestedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                accountResponses,
                roles
        );
    }

    @Transactional
    public void updateUserStatus(
            String userId,
            UserStatus status,
            String currentUserId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (userId.equals(currentUserId)
                && status == UserStatus.DISABLED) {
            throw new BadRequestException(
                    "Admin cannot disable themselves"
            );
        }

        if (user.getStatus() == status) {
            throw new BadRequestException(
                    "User already has this status"
            );
        }

        if (status == UserStatus.PENDING_DELETION) {
            throw new BadRequestException(
                    "PENDING_DELETION cannot be set by admin"
            );
        }

        if (status == UserStatus.DISABLED) {
            refreshTokenRepository.revokeAllByUserId(userId);
        }

        user.setStatus(status);
    }
}