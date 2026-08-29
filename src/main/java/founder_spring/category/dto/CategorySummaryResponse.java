package founder_spring.category.dto;

import founder_spring.category.entity.CategoryType;

public class CategorySummaryResponse {

    private String id;
    private String name;
    private CategoryType type;

    public CategorySummaryResponse() {
    }

    public CategorySummaryResponse(
            String id,
            String name,
            CategoryType type
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }
}