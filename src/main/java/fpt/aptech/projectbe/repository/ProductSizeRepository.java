package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.entites.ProductSizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, ProductSizeId> {
    List<ProductSize> findByProductId(Integer productId);
    ProductSize findByProductIdAndSizeId(Integer productId, Integer sizeId);
} 