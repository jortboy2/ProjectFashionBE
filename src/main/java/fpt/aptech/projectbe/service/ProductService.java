package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Integer id);
    Product save(Product product);
    void deleteById(Integer id);
    Product update(Product product);
} 