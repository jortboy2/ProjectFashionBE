package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng để cập nhật");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword()); // nên mã hóa nếu dùng bcrypt
        }

        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return ResponseEntity.ok(userService.update(existingUser));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (userService.findById(id) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng để xoá");
        }
        userService.deleteById(id);
        return ResponseEntity.ok("Xoá người dùng thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Username và password không được để trống");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng");
        }

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body("Mật khẩu không đúng");
        }

        // Tạo response chứa thông tin user và role
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getId());
        response.put("message", "Đăng nhập thành công");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Kiểm tra username đã tồn tại chưa
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username đã tồn tại");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        // Tạo user mới với role mặc định là 2
        User savedUser = userService.createUserWithDefaultRole(user);

        // Tạo response chứa thông tin user đã đăng ký
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().getId());
        response.put("message", "Đăng ký thành công");

        return ResponseEntity.ok(response);
    }
}
