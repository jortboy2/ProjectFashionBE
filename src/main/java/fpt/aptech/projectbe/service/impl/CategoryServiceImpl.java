package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Category;
import fpt.aptech.projectbe.repository.CategoryRepository;
import fpt.aptech.projectbe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Lấy tất cả sản phẩm
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 2. Tìm sản phẩm theo ID
    @Override
    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // 3. Lưu sản phẩm mới
    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // 4. Xoá sản phẩm theo ID
    @Override
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }

    // 5. Cập nhật sản phẩm
    @Override
    public Category update(Category category) {
        return categoryRepository.save(category);
    }
}
