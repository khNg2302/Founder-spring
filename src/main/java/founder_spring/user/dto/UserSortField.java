package founder_spring.user.dto;

public enum UserSortField {

    CREATED_AT("createdAt"),
    NAME("name"),
    STATUS("status");

    private final String property;

    UserSortField(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}