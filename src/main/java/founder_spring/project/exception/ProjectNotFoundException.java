package founder_spring.project.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String id) {
        super("Project not found: " + id);
    }
}