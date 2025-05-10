package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    Discount findByCode(String code);
    boolean existsByCode(String code);
}
