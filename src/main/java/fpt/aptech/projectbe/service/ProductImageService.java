package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.ProductImage;
import fpt.aptech.projectbe.entites.Product;
import java.util.List;
import java.util.Optional;

public interface ProductImageService {
    List<ProductImage> findAll();
    Optional<ProductImage> findById(Integer id);
    ProductImage save(ProductImage productImage);
    List<ProductImage> saveAll(List<ProductImage> productImages);
    void deleteById(Integer id);
    ProductImage update(ProductImage productImage);
    List<ProductImage> findByProduct(Product product);
} 