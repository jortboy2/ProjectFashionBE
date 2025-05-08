package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại");
        }
        return ResponseEntity.ok(orderService.findByUser(user));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Order>> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        return ResponseEntity.ok(orderService.findByPaymentStatus(paymentStatus));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.save(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        if (orderService.findById(id) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng để cập nhật");
        }
        order.setId(id);
        return ResponseEntity.ok(orderService.update(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        if (orderService.findById(id) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng để xoá");
        }
        orderService.deleteById(id);
        return ResponseEntity.ok("Xoá đơn hàng thành công");
    }
}
