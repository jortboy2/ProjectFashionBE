package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.entites.ProductSize;
import java.util.List;
import java.util.Optional;

public interface ProductSizeService {
    List<ProductSize> findAll();
    Optional<ProductSize> findById(Integer id);
    ProductSize save(ProductSize productSize);
    ProductSize update(ProductSize productSize);
    void deleteById(Integer id);
    void delete(ProductSize productSize);
    
    // New methods using entities
    ProductSize findByProductAndSize(Product product, Size size);
    List<ProductSize> findByProduct(Product product);
    List<ProductSize> findBySize(Size size);
    
    // Old methods using IDs
    List<ProductSize> findByProductId(Integer productId);
    ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId);
    void updateStock(Integer productId, Integer sizeId, Integer quantity);
} 