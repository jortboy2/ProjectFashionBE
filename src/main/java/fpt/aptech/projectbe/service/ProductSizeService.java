package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.ProductSize;
import java.util.List;

public interface ProductSizeService {
    List<ProductSize> findByProductId(Integer productId);
    ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId);
    ProductSize save(ProductSize productSize);
    void delete(ProductSize productSize);
    void updateStock(Integer productId, Integer sizeId, Integer quantity);
} 