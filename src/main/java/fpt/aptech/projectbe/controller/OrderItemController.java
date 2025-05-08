package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.service.OrderItemService;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@CrossOrigin(origins = "*")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Integer id) {
        return orderItemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrder(@PathVariable Integer orderId) {
        return orderService.findById(orderId)
                .map(order -> ResponseEntity.ok(orderItemService.findByOrder(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProduct(@PathVariable Integer productId) {
        return productService.findById(productId)
                .map(product -> ResponseEntity.ok(orderItemService.findByProduct(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        return ResponseEntity.ok(orderItemService.save(orderItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Integer id, @RequestBody OrderItem orderItem) {
        return orderItemService.findById(id)
                .map(existingOrderItem -> {
                    orderItem.setId(id);
                    return ResponseEntity.ok(orderItemService.update(orderItem));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Integer id) {
        return orderItemService.findById(id)
                .map(orderItem -> {
                    orderItemService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 