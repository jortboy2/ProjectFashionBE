package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Role;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.repository.RoleRepository;
import fpt.aptech.projectbe.repository.UserRepository;
import fpt.aptech.projectbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    // 1. Lấy tất cả người dùng
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 2. Tìm người dùng theo ID
    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // 3. Lưu người dùng mới
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // 4. Xoá người dùng theo ID
    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    // 5. Cập nhật người dùng
    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    // 6. Tìm theo username
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 7. Tìm theo email
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUserWithDefaultRole(User user) {
        Role defaultRole = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRole(defaultRole);
        user.setActive(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }
    @Override
    public User findByVerificationToken(String token) {
        return userRepository.findByVerificationToken(token);
    }

}
