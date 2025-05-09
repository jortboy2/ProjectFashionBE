package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Category;
import fpt.aptech.projectbe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // Cho phép gọi từ front-end nếu cần
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Lấy danh sách tất cả category
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    // 2. Lấy category theo ID
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryService.findById(id);
    }

    // 3. Tạo mới category
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    // 4. Xoá category theo ID
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteById(id);
    }

    // 5. Cập nhật category (có kiểm tra tồn tại)
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        Category existing = categoryService.findById(id);
        if (existing == null) {
            throw new RuntimeException("Category with ID " + id + " not found.");
        }
        // Đảm bảo ID đúng
        category.setId(id);
        return categoryService.update(category);
    }
}
