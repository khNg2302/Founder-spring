package founder_spring.category.dto;

import founder_spring.category.entity.CategoryType;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {

    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    private CategoryType type;

    private String parentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}