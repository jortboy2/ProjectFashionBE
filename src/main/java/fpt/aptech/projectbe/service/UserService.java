package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User save(User user);
    void deleteById(Integer id);
    User update(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    
    // Thêm phương thức tạo user với role mặc định là 2
    User createUserWithDefaultRole(User user);
} 