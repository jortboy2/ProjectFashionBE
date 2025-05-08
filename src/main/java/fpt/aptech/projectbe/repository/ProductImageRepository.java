package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.ProductImage;
import fpt.aptech.projectbe.entites.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct(Product product);
} 