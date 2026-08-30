package founder_spring.common.exception;

import founder_spring.category.exception.CategoryNotFoundException;
import founder_spring.category.exception.InvalidCategoryException;
import founder_spring.project.exception.ProjectNotFoundException;
import founder_spring.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {

        String message = "Invalid request body";

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {

            Class<?> enumClass =
                    invalidFormatException.getTargetType();

            Object[] values =
                    enumClass.getEnumConstants();

            String allowedValues =
                    Arrays.stream(values)
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));

            message =
                    "Invalid value '"
                            + invalidFormatException.getValue()
                            + "'. Allowed values: "
                            + allowedValues;
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_BODY",
                message
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Validation failed");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "CATEGORY_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCategory(
            InvalidCategoryException ex
    ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_CATEGORY",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "PROJECT_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex
    ) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "USER_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message
    ) {

        ErrorResponse response = new ErrorResponse(
                status.value(),
                code,
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
