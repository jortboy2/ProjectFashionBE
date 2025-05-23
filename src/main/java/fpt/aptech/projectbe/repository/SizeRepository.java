package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
    Size findByName(String name);
} 