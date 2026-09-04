package founder_spring.authorization.config;

public final class RbacPermission {

    private RbacPermission() {}

    public static final String USER_READ = "user:read";
    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";

    public static final String USER_ROLE_ASSIGN = "user:role:assign";
    public static final String USER_ROLE_REMOVE = "user:role:remove";

    public static final String ROLE_READ = "role:read";
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";

    public static final String ROLE_PERMISSION_ASSIGN =
            "role:permission:assign";

    public static final String ROLE_PERMISSION_REMOVE =
            "role:permission:remove";

    public static final String PERMISSION_READ = "permission:read";
    public static final String PERMISSION_CREATE = "permission:create";
    public static final String PERMISSION_UPDATE = "permission:update";
    public static final String PERMISSION_DELETE = "permission:delete";

    public static final String AUDIENCE_TYPE_READ = "audience_type:read";
    public static final String AUDIENCE_TYPE_CREATE = "audience_type:create";
    public static final String AUDIENCE_TYPE_UPDATE = "audience_type:update";
    public static final String AUDIENCE_TYPE_DELETE = "audience_type:delete";
}

