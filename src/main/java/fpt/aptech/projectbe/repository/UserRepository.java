package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByRoleId(int roleId);
    User findByVerificationToken(String token);

} 