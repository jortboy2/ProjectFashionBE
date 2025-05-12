package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.OrderDTO;
import fpt.aptech.projectbe.dto.OrderItemDTO;
import fpt.aptech.projectbe.dto.PaymentDTO;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.entites.Size;
import fpt.aptech.projectbe.entites.Payment;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.mapper.OrderMapper;
import fpt.aptech.projectbe.mapper.PaymentMapper;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.OrderItemService;
import fpt.aptech.projectbe.service.UserService;
import fpt.aptech.projectbe.service.ProductService;
import fpt.aptech.projectbe.service.SizeService;
import fpt.aptech.projectbe.service.PaymentService;
import fpt.aptech.projectbe.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductSizeService productSizeService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    private String generateOrderCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        
        return code.toString();
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        List<OrderDTO> orderDTOs = orders.stream()
            .map(orderMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        return orderService.findById(id)
            .map(orderMapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Integer userId) {
        try {
            List<Order> orders = orderService.findByUserid(userId);


            List<OrderDTO> orderDTOs = orders.stream()
                    .map(orderMapper::toDTO)
                    .collect(Collectors.toList());
            if (orders.isEmpty()) {
                return ResponseEntity.ok(orderDTOs);
            }
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<?> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        return ResponseEntity.ok(orderService.findByPaymentStatus(paymentStatus));
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<?> getOrderByOrderCode(@PathVariable String orderCode) {
        try {
            Optional<Order> orderOpt = orderService.findByOrderCode(orderCode);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy đơn hàng với mã: " + orderCode));
            }
            return ResponseEntity.ok(orderMapper.toDTO(orderOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi tìm kiếm đơn hàng: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            // Validate required fields
            if (orderDTO.getUserId() == null || orderDTO.getTotal() == null || orderDTO.getOrderItems() == null) {
                return ResponseEntity.badRequest().body("User ID, total amount and order items are required");
            }

            // Find user
            User user = userService.findById(orderDTO.getUserId());
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setTotal(orderDTO.getTotal());
            order.setStatus("Đang xử lý");
            order.setPaymentStatus(orderDTO.getPaymentStatus() != null ? orderDTO.getPaymentStatus() : "Chưa thanh toán");
            order.setReceiverName(orderDTO.getReceiverName());
            order.setReceiverEmail(orderDTO.getReceiverEmail());
            order.setReceiverPhone(orderDTO.getReceiverPhone());
            order.setReceiverAddress(orderDTO.getReceiverAddress());
            order.setOrderCode(generateOrderCode());
            // Save order first to get ID
            Order savedOrder = orderService.save(order);

            // Create and save order items
            if (orderDTO.getOrderItems() != null) {
                for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                    // Validate product and size
                    if (itemDTO.getProductId() == null || itemDTO.getSizeId() == null) {
                        return ResponseEntity.badRequest().body("Product ID and Size ID are required for each order item");
                    }

                    // Find product and size
                    Product product = productService.findById(itemDTO.getProductId());
                    Size size = sizeService.findById(itemDTO.getSizeId());

                    if (product == null) {
                        return ResponseEntity.badRequest().body("Product not found with ID: " + itemDTO.getProductId());
                    }
                    if (size == null) {
                        return ResponseEntity.badRequest().body("Size not found with ID: " + itemDTO.getSizeId());
                    }

                    // Check product size stock
                    ProductSize productSize = productSizeService.findByProductAndSize(product, size);
                    if (productSize == null) {
                        return ResponseEntity.badRequest().body("Product size combination not found for product: " + product.getName() + " and size: " + size.getName());
                    }
                    
                    if (productSize.getStock() < itemDTO.getQuantity()) {
                        return ResponseEntity.badRequest().body("Insufficient stock for product: " + product.getName() + 
                            " size: " + size.getName() + ". Available: " + productSize.getStock() + 
                            ", Requested: " + itemDTO.getQuantity());
                    }

                    // Create order item
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setProduct(product);
                    orderItem.setSize(size);
                    orderItem.setQuantity(itemDTO.getQuantity());
                    orderItem.setPrice(itemDTO.getPrice());

                    // Save order item
                    orderItemService.save(orderItem);
                }
            }

            return ResponseEntity.ok(orderMapper.toDTO(savedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Integer id, @RequestBody OrderDTO orderDTO) {
        return orderService.findById(id)
            .map(existingOrder -> {
                // Update fields from DTO
                existingOrder.setTotal(orderDTO.getTotal());
                existingOrder.setStatus(orderDTO.getStatus());
                existingOrder.setPaymentStatus(orderDTO.getPaymentStatus());
                // Update other fields as needed
                
                Order updatedOrder = orderService.update(existingOrder);
                return ResponseEntity.ok(orderMapper.toDTO(updatedOrder));
            })
            .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
            }
            
            Order order = orderOpt.get();
            // Chỉ cho phép hủy đơn hàng ở trạng thái pending
            if (!"Đang xử lý".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể hủy đơn hàng ở trạng thái pending");
            }
            
            order.setStatus("cancelled");
            return ResponseEntity.ok(orderService.update(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        try {
            orderService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<?> getOrderItems(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
        }
        List<OrderItem> orderItems = orderItemService.findByOrder(orderOpt.get());
        return ResponseEntity.ok(orderItems);
    }

    @PutMapping(value = "/{id}/confirm", produces = "application/json")
    public ResponseEntity<?> confirmOrder(
            @PathVariable("id") Integer id,
            @RequestParam(value = "paymentMethod", required = true) String paymentMethod) {
        try {
            // Validate payment method
            if (!"momo".equals(paymentMethod) && !"cash".equals(paymentMethod)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phương thức thanh toán không hợp lệ. Chỉ chấp nhận 'momo' hoặc 'cash'"));
            }

            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy đơn hàng với ID: " + id));
            }
            
            Order order = orderOpt.get();
            
            // Kiểm tra trạng thái đơn hàng
            if (!"Đang xử lý".equals(order.getStatus())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chỉ có thể xác nhận đơn hàng ở trạng thái đang đợi xác nhận"));
            }

            // Kiểm tra và cập nhật số lượng sản phẩm
            List<OrderItem> orderItems = orderItemService.findByOrder(order);
            for (OrderItem item : orderItems) {
                ProductSize productSize = productSizeService.findByProductAndSize(item.getProduct(), item.getSize());
                if (productSize == null) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Không tìm thấy thông tin số lượng sản phẩm " + item.getProduct().getName() + " size " + item.getSize().getName()));
                }
                
                if (productSize.getStock() < item.getQuantity()) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Sản phẩm " + item.getProduct().getName() + " size " + item.getSize().getName() + " không đủ số lượng trong kho"));
                }
                
                // Giảm số lượng sản phẩm
                productSize.setStock(productSize.getStock() - item.getQuantity());
                productSizeService.save(productSize);
            }
            
            // Cập nhật trạng thái đơn hàng
            order.setStatus("Xác nhận");
            Order updatedOrder = orderService.update(order);
            
            // Tạo payment mới
            Payment payment = new Payment();
            payment.setOrder(updatedOrder);
            payment.setAmount(updatedOrder.getTotal());
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus(paymentMethod.equals("momo") ? "Đã thanh toán" : "Chưa thanh toán");
            
            // Lưu payment
            Payment savedPayment = paymentService.save(payment);
            
            // Chuyển đổi sang DTO
            OrderDTO orderDTO = orderMapper.toDTO(updatedOrder);
            PaymentDTO paymentDTO = paymentMapper.toDTO(savedPayment);
            
            // Trả về thông tin đơn hàng đã cập nhật và payment mới
            return ResponseEntity.ok(Map.of(
                "message", "Xác nhận đơn hàng thành công",
                "order", orderDTO,
                "payment", paymentDTO
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi xác nhận đơn hàng: " + e.getMessage()));
        }
    }
}
