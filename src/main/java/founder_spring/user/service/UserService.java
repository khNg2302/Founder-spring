package founder_spring.user.service;

import founder_spring.user.dto.UpdateUserRequest;
import founder_spring.user.dto.UserResponse;
import founder_spring.user.entity.User;
import founder_spring.user.exception.UserNotFoundException;
import founder_spring.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}