package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.OrderItemDTO;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.mapper.OrderMapper;
import fpt.aptech.projectbe.service.OrderItemService;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.ProductService;
import fpt.aptech.projectbe.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private SizeService sizeService;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.findAll();
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
            .map(orderMapper::toOrderItemDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orderItemDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Integer id) {
        return orderItemService.findById(id)
            .map(orderMapper::toOrderItemDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderItemsByOrderId(@PathVariable Integer orderId) {
        Order order = orderService.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found");
        }
        List<OrderItem> orderItems = orderItemService.findByOrder(order);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
            .map(orderMapper::toOrderItemDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orderItemDTOs);
    }

    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        try {
            // Validate required fields
            if (orderItemDTO.getOrderId() == null || orderItemDTO.getProductId() == null || 
                orderItemDTO.getSizeId() == null || orderItemDTO.getQuantity() == null || 
                orderItemDTO.getPrice() == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            // Find order
            Order order = orderService.findById(orderItemDTO.getOrderId()).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest().body("Order not found");
            }

            // Find product and size
            Product product = productService.findById(orderItemDTO.getProductId());
            Size size = sizeService.findById(orderItemDTO.getSizeId());

            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found");
            }
            if (size == null) {
                return ResponseEntity.badRequest().body("Size not found");
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setSize(size);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setPrice(orderItemDTO.getPrice());

            // Save order item
            OrderItem savedOrderItem = orderItemService.save(orderItem);
            return ResponseEntity.ok(orderMapper.toOrderItemDTO(savedOrderItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating order item: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Integer id, @RequestBody OrderItemDTO orderItemDTO) {
        try {
            Optional<OrderItem> existingOrderItemOpt = orderItemService.findById(id);
            if (existingOrderItemOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Order item not found");
            }

            OrderItem existingOrderItem = existingOrderItemOpt.get();

            // Update fields
            if (orderItemDTO.getQuantity() != null) {
                existingOrderItem.setQuantity(orderItemDTO.getQuantity());
            }
            if (orderItemDTO.getPrice() != null) {
                existingOrderItem.setPrice(orderItemDTO.getPrice());
            }

            // Update product if provided
            if (orderItemDTO.getProductId() != null) {
                Product product = productService.findById(orderItemDTO.getProductId());
                if (product == null) {
                    return ResponseEntity.badRequest().body("Product not found");
                }
                existingOrderItem.setProduct(product);
            }

            // Update size if provided
            if (orderItemDTO.getSizeId() != null) {
                Size size = sizeService.findById(orderItemDTO.getSizeId());
                if (size == null) {
                    return ResponseEntity.badRequest().body("Size not found");
                }
                existingOrderItem.setSize(size);
            }

            OrderItem updatedOrderItem = orderItemService.update(existingOrderItem);
            return ResponseEntity.ok(orderMapper.toOrderItemDTO(updatedOrderItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating order item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Integer id) {
        try {
            Optional<OrderItem> orderItemOpt = orderItemService.findById(id);
            if (orderItemOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Order item not found");
            }
            orderItemService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting order item: " + e.getMessage());
        }
    }
} 