package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.service.impl.EmailService;
import fpt.aptech.projectbe.service.PasswordService;
import fpt.aptech.projectbe.service.UserService;
import fpt.aptech.projectbe.service.impl.OtpService;
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

    @Autowired
    private PasswordService passwordService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;
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
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordService.encodePassword(user.getPassword()));
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
            existingUser.setPassword(passwordService.encodePassword(user.getPassword()));
        }

        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        // ❌ Không set countWheel ở đây!
        // => nên thêm dòng này:
        existingUser.setCountWheel(user.getCountWheel());

        return ResponseEntity.ok(userService.update(existingUser));
    }



    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        Integer userId = null;
        try {
            userId = Integer.parseInt(request.get("userId"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID người dùng không hợp lệ");
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null || userId == null) {
            return ResponseEntity.badRequest().body("Thông tin không đầy đủ");
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng");
        }

        if (!passwordService.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordService.encodePassword(newPassword));
        userService.update(user);

        return ResponseEntity.ok("Đổi mật khẩu thành công");
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

        if (!passwordService.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu không đúng");
        }
        if (!user.isActive()) {
            return ResponseEntity.badRequest().body("Tài khoản chưa được xác thực. Vui lòng kiểm tra email.");
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
        // Kiểm tra email đã tồn tại chưa
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        // Kiểm tra độ dài mật khẩu
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordService.encodePassword(user.getPassword()));

        // Tạo user mới với role mặc định là 2
        User savedUser = userService.createUserWithDefaultRole(user);
        String subject = "Xác thực email";
        String Text = "Click vào đường dẫn để xác thực";
        String verificationLink = "http://localhost:8080/api/users/verify-email?token=" + savedUser.getVerificationToken();
        emailService.sendOtpEmail(subject, Text,savedUser.getEmail(), "Bấm vào link để xác thực tài khoản: " + verificationLink);
        // Tạo response chứa thông tin user đã đăng ký
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().getId());
        response.put("message", "Đăng ký thành công");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        User user = userService.findByVerificationToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Token không hợp lệ");
        }

        user.setActive(true);
        user.setVerificationToken(null); // clear token
        userService.update(user);

        return ResponseEntity.ok("Tài khoản đã được xác thực thành công!");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng với email này");
        }

        String otp = otpService.generateOtp(email);
        String text = "Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút.";
        String subject  = "Mã OTP khôi phục mật khẩu";
        emailService.sendOtpEmail(subject, text,email, otp);
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (otpService.validateOtp(email, otp)) {
            otpService.clearOtp(email);
            return ResponseEntity.ok("OTP hợp lệ. Tiến hành đặt lại mật khẩu.");
        } else {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng");
        }

        user.setPassword(passwordService.encodePassword(newPassword));
        userService.update(user);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }
}
