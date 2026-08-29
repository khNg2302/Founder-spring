package founder_spring.category.service;

import founder_spring.category.dto.CategoryResponse;
import founder_spring.category.dto.CreateCategoryRequest;
import founder_spring.category.dto.UpdateCategoryRequest;
import founder_spring.category.entity.Category;
import founder_spring.category.exception.CategoryNotFoundException;
import founder_spring.category.repository.CategoryRepository;
import founder_spring.common.util.CuidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CuidGenerator cuidGenerator;

    public CategoryService(
            CategoryRepository categoryRepository,
            CuidGenerator cuidGenerator
    ) {
        this.categoryRepository = categoryRepository;
        this.cuidGenerator = cuidGenerator;
    }

    private CategoryResponse toResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());

        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
        }

        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Category findById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public CategoryResponse create(CreateCategoryRequest request) {

        Category category = new Category();

        category.setId(cuidGenerator.generate());
        category.setName(request.getName());
        category.setType(request.getType());

        if (request.getParentId() != null) {
            Category parent = findById(request.getParentId());
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }

    public Category update(String id, UpdateCategoryRequest request) {

        Category category = findById(id);

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getType() != null) {
            category.setType(request.getType());
        }

        if (request.getParentId() != null) {
            Category parent = findById(request.getParentId());
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public void delete(String id) {

        Category category = findById(id);

        categoryRepository.delete(category);
    }
}