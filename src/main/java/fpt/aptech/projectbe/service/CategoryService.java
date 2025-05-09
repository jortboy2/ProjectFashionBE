package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category findById(Integer id);
    Category save(Category category);
    void deleteById(Integer id);
    Category update(Category category);
} 