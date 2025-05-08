package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
} 