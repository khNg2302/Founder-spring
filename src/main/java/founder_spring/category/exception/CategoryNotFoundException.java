package founder_spring.category.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String id) {
        super("Category not found: " + id);
    }
}