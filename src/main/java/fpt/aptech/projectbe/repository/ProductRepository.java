package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId")
    List<Product> findRelatedProducts(@Param("categoryId") Integer categoryId, @Param("productId") Integer productId);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN p.category c
        LEFT JOIN p.productSizes ps
        LEFT JOIN ps.size s
        WHERE (:category IS NULL OR c.name = :category)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:sizes IS NULL OR s.name IN :sizes)
    """)
    List<Product> filterProducts(
        @Param("category") String category,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice,
        @Param("sizes") java.util.List<String> sizes
    );
} 