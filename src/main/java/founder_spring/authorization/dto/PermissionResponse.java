package founder_spring.authorization.dto;

public record PermissionResponse(
        String id,
        String name,
        String description
) {}