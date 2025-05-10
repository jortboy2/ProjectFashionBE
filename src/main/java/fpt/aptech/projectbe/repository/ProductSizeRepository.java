package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.entites.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    ProductSize findByProductAndSize(Product product, Size size);
    List<ProductSize> findByProduct(Product product);
    List<ProductSize> findBySize(Size size);
    
    List<ProductSize> findByProductId(Integer productId);
    ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId);
} 