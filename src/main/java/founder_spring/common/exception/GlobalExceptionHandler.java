package founder_spring.common.exception;

import founder_spring.project.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleProjectNotFound(
            ProjectNotFoundException exception
    ) {
        return Map.of(
                "message", exception.getMessage()
        );
    }
}