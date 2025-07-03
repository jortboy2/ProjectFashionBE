package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.WheelPrize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrizeRepository extends JpaRepository<WheelPrize, Integer> {
}
