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
import fpt.aptech.projectbe.service.VNPayService;
import fpt.aptech.projectbe.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

    @Autowired
    private VNPayService vnPayService;
    
    @Autowired
    private EmailService emailService;

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
        try {
            List<Order> orders = orderService.findAll();
            List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
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
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.findByStatus(status);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<?> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        List<Order> orders = orderService.findByPaymentStatus(paymentStatus);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<?> getOrdersByPaymentMethod(@PathVariable String paymentMethod) {
        List<Order> orders = orderService.findByPaymentMethod(paymentMethod);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
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
            if (orderDTO.getUserId() == null ||
                    orderDTO.getTotal() == null || orderDTO.getTotal().compareTo(BigDecimal.ZERO) <= 0 ||
                    orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID, total amount (> 0), and order items are required");
            }

            // Optional: Check total amount matches sum of order items
            BigDecimal calculatedTotal = BigDecimal.ZERO;

            for (OrderItemDTO item : orderDTO.getOrderItems()) {
                if (item.getPrice() == null || item.getQuantity() == null) {
                    return ResponseEntity.badRequest().body("Each order item must include price and quantity");
                }

                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                calculatedTotal = calculatedTotal.add(itemTotal);
            }

// Kiểm tra tổng khớp với total
            if (orderDTO.getTotal().compareTo(calculatedTotal) != 0) {
                return ResponseEntity.badRequest().body("Total amount does not match the sum of order items");
            }

            // Validate payment method
            if (orderDTO.getPaymentMethod() == null ||
                    (!"vnpay".equals(orderDTO.getPaymentMethod()) && !"cash".equals(orderDTO.getPaymentMethod()))) {
                return ResponseEntity.badRequest().body("Payment method must be either 'vnpay' or 'cash'");
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
            order.setPaymentMethod(orderDTO.getPaymentMethod());

            if ("vnpay".equals(orderDTO.getPaymentMethod())) {
                order.setPaymentStatus("Chờ thanh toán");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expired = now.plusHours(24);
                order.setExpiredAt(Date.from(expired.atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                order.setPaymentStatus("Chưa thanh toán");
            }

            order.setReceiverName(orderDTO.getReceiverName());
            order.setReceiverEmail(orderDTO.getReceiverEmail());
            order.setReceiverPhone(orderDTO.getReceiverPhone());
            order.setReceiverAddress(orderDTO.getReceiverAddress());
            order.setOrderCode(generateOrderCode());

            // Save order to get ID
            Order savedOrder = orderService.save(order);

            // Create and save order items
            for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                if (itemDTO.getProductId() == null || itemDTO.getSizeId() == null) {
                    return ResponseEntity.badRequest().body("Product ID and Size ID are required for each order item");
                }

                Product product = productService.findById(itemDTO.getProductId());
                Size size = sizeService.findById(itemDTO.getSizeId());

                if (product == null) {
                    return ResponseEntity.badRequest().body("Product not found with ID: " + itemDTO.getProductId());
                }
                if (size == null) {
                    return ResponseEntity.badRequest().body("Size not found with ID: " + itemDTO.getSizeId());
                }

                ProductSize productSize = productSizeService.findByProductAndSize(product, size);
                if (productSize == null) {
                    return ResponseEntity.badRequest().body("Product size combination not found for product: " + product.getName() + " and size: " + size.getName());
                }

                if (productSize.getStock() < itemDTO.getQuantity()) {
                    return ResponseEntity.badRequest().body("Insufficient stock for product: " + product.getName() +
                            " size: " + size.getName() + ". Available: " + productSize.getStock() +
                            ", Requested: " + itemDTO.getQuantity());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProduct(product);
                orderItem.setSize(size);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setPrice(itemDTO.getPrice());

                orderItemService.save(orderItem);

                // Decrease stock
                productSize.setStock(productSize.getStock() - itemDTO.getQuantity());
                productSizeService.save(productSize);
            }

            // Send confirmation email
            try {
                emailService.sendOrderConfirmationEmail(savedOrder);
            } catch (Exception e) {
                System.err.println("Error sending confirmation email: " + e.getMessage());
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
            
            // Chỉ cho phép hủy đơn hàng ở trạng thái đang xử lý
            if (!"Đang xử lý".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể hủy đơn hàng ở trạng thái đang xử lý");
            }

            // Lấy danh sách order items
            List<OrderItem> orderItems = orderItemService.findByOrder(order);
            
            // Hoàn trả số lượng sản phẩm
            for (OrderItem item : orderItems) {
                ProductSize productSize = productSizeService.findByProductAndSize(item.getProduct(), item.getSize());
                if (productSize != null) {
                    // Tăng số lượng sản phẩm lên
                    productSize.setStock(productSize.getStock() + item.getQuantity());
                    productSizeService.save(productSize);
                }
            }
            
            // Cập nhật trạng thái đơn hàng thành đã hủy
            order.setStatus("Đã hủy");
            Order updatedOrder = orderService.update(order);
            
            return ResponseEntity.ok(Map.of(
                "message", "Hủy đơn hàng thành công",
                "order", orderMapper.toDTO(updatedOrder)
            ));
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
            if (!"vnpay".equals(paymentMethod) && !"cash".equals(paymentMethod)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Phương thức thanh toán không hợp lệ. Chỉ chấp nhận 'vnpay' hoặc 'cash'"));
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
                    .body(Map.of("message", "Chỉ có thể xác nhận đơn hàng ở trạng thái đang xử lý"));
            }
            
            // Cập nhật trạng thái đơn hàng
            order.setStatus("Xác nhận");
            Order updatedOrder = orderService.update(order);
            
            // Tạo payment mới với transaction code
            Payment payment = new Payment();
            payment.setOrder(updatedOrder);
            payment.setAmount(updatedOrder.getTotal());
            payment.setPaymentMethod(paymentMethod);
            payment.setStatus(paymentMethod.equals("vnpay") ? "Đã thanh toán" : "Chưa thanh toán");
            
            // Lưu payment
            Payment savedPayment = paymentService.save(payment);
            
            // Chuyển đổi sang DTO
            OrderDTO orderDTO = orderMapper.toDTO(updatedOrder);
            PaymentDTO paymentDTO = paymentMapper.toDTO(savedPayment);
            
            // Trả về thông tin đơn hàng đã cập nhật và payment mới
            return ResponseEntity.ok(Map.of(
                "message", "Xác nhận đơn hàng thành công",
                "order", orderDTO,
                "payment", paymentDTO,
                "transactionCode", savedPayment.getTransactionCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi xác nhận đơn hàng: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable Integer orderId,
            @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            Optional<Order> orderOpt = orderService.findById(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy đơn hàng",
                        "code", "ORDER_NOT_FOUND"
                    ));
            }

            Order order = orderOpt.get();
            
            // Kiểm tra trạng thái thanh toán
            if ("Đã thanh toán".equals(order.getPaymentStatus())) {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", "Đơn hàng này đã được thanh toán",
                        "code", "ORDER_ALREADY_PAID"
                    ));
            }

            // Lấy returnUrl từ request body nếu có
            String returnUrl = null;
            if (requestBody != null && requestBody.containsKey("returnUrl")) {
                returnUrl = (String) requestBody.get("returnUrl");
            }

            // Nếu đang chờ thanh toán
            if ("Chờ thanh toán".equals(order.getPaymentStatus())) {
                // Kiểm tra thời gian hết hạn
                if (order.getExpiredAt() != null && order.getExpiredAt().before(new Date())) {
                    // Nếu đã hết hạn, cập nhật lại thời gian hết hạn
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime expired = now.plusHours(24);
                    order.setExpiredAt(Date.from(expired.atZone(ZoneId.systemDefault()).toInstant()));
                    orderService.update(order);
                }

                // Lấy payment hiện tại
                List<Payment> existingPayments = paymentService.findByOrder(order);
                if (!existingPayments.isEmpty()) {
                    Payment existingPayment = existingPayments.get(0);
                    
                    // Tạo URL thanh toán với payment hiện tại
                    String paymentUrl = vnPayService.createPaymentUrl(
                        orderId.longValue(), 
                        order.getTotal().longValue(),
                        returnUrl
                    );

                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Lấy URL thanh toán thành công",
                        "data", Map.of(
                            "paymentUrl", paymentUrl,
                            "orderId", orderId,
                            "amount", order.getTotal(),
                            "transactionCode", existingPayment.getTransactionCode(),
                            "expiredAt", order.getExpiredAt()
                        )
                    ));
                }
            }

            // Tạo payment mới nếu chưa có
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotal());
            payment.setPaymentMethod("vnpay");
            payment.setStatus("Chờ thanh toán");
            
            // Lưu payment
            Payment savedPayment = paymentService.save(payment);

            // Tạo URL thanh toán mới
            String paymentUrl = vnPayService.createPaymentUrl(
                orderId.longValue(), 
                order.getTotal().longValue(),
                returnUrl
            );

            // Cập nhật trạng thái đơn hàng
            order.setPaymentStatus("Chờ thanh toán");
            // Set expired_at = now + 24h
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = now.plusHours(24);
            order.setExpiredAt(Date.from(expired.atZone(ZoneId.systemDefault()).toInstant()));
            orderService.update(order);

            // Trả về response
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tạo URL thanh toán thành công",
                "data", Map.of(
                    "paymentUrl", paymentUrl,
                    "orderId", orderId,
                    "amount", order.getTotal(),
                    "transactionCode", savedPayment.getTransactionCode(),
                    "expiredAt", order.getExpiredAt()
                )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi xử lý thanh toán: " + e.getMessage(),
                    "code", "INTERNAL_SERVER_ERROR"
                ));
        }
    }

    @GetMapping("/payment/vnpay/return")
    public ResponseEntity<?> vnpayReturnWeb(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") String orderId,
            @RequestParam("vnp_TransactionStatus") String transactionStatus) {
        try {
            // Kiểm tra mã phản hồi từ VNPay
            System.out.println("transactionStatus"+transactionStatus);
            System.out.println("oke"+ responseCode);
            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                // Thanh toán thành công
                Optional<Order> orderOpt = orderService.findById(Integer.parseInt(orderId));
                if (orderOpt.isPresent()) {
                    Order existingOrder = orderOpt.get();
                    
                    // Cập nhật trạng thái thanh toán và đơn hàng
                    existingOrder.setPaymentStatus("Đã thanh toán");
                    existingOrder.setStatus("Xác nhận");
                    
                    // Lưu cập nhật đơn hàng
                    Order updatedOrder = orderService.update(existingOrder);
                    if (updatedOrder == null) {
                        return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái đơn hàng");
                    }

                    // Tìm payment record hiện tại hoặc tạo mới
                    List<Payment> existingPayments = paymentService.findByOrder(existingOrder);
                    Payment payment;
                    if (!existingPayments.isEmpty()) {
                        payment = existingPayments.get(0);
                    } else {
                        payment = new Payment();
                    }
                    
                    // Cập nhật thông tin payment
                    payment.setOrder(updatedOrder);
                    payment.setAmount(updatedOrder.getTotal());
                    payment.setPaymentMethod("vnpay");
                    payment.setStatus("Đã thanh toán");
                    Payment savedPayment = paymentService.save(payment);
                    
                    if (savedPayment == null) {
                        return ResponseEntity.badRequest().body("Lỗi khi lưu thông tin thanh toán");
                    }

                    return ResponseEntity.ok(Map.of(
                        "message", "Thanh toán thành công",
                        "order", orderMapper.toDTO(updatedOrder),
                        "redirectUrl", "http://localhost:5173/order-success"
                    ));
                }
            }

            // Thanh toán thất bại
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Thanh toán thất bại",
                "responseCode", responseCode,
                "transactionStatus", transactionStatus,
                "redirectUrl", "http://localhost:5173/order-failed"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xử lý kết quả thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/payment/vnpay/return/mobile")
    public ResponseEntity<?> vnpayReturnMobile(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") String orderId,
            @RequestParam("vnp_TransactionStatus") String transactionStatus) {
        try {
            // Kiểm tra mã phản hồi từ VNPay
            System.out.println("transactionStatus"+transactionStatus);
            System.out.println("oke"+ responseCode);
            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                // Thanh toán thành công
                Optional<Order> orderOpt = orderService.findById(Integer.parseInt(orderId));
                if (orderOpt.isPresent()) {
                    Order existingOrder = orderOpt.get();
                    
                    // Cập nhật trạng thái thanh toán và đơn hàng
                    existingOrder.setPaymentStatus("Đã thanh toán");
                    existingOrder.setStatus("Xác nhận");
                    
                    // Lưu cập nhật đơn hàng
                    Order updatedOrder = orderService.update(existingOrder);
                    if (updatedOrder == null) {
                        return ResponseEntity.ok(Map.of(
                            "success", false,
                            "message", "Lỗi khi cập nhật trạng thái đơn hàng",
                            "data", Map.of(
                                "redirectUrl", "fashionmobile://payment/vnpay/return/mobile?success=false&error=UPDATE_ORDER_FAILED"
                            )
                        ));
                    }

                    // Tìm payment record hiện tại hoặc tạo mới
                    List<Payment> existingPayments = paymentService.findByOrder(existingOrder);
                    Payment payment;
                    if (!existingPayments.isEmpty()) {
                        payment = existingPayments.get(0);
                    } else {
                        payment = new Payment();
                    }
                    
                    // Cập nhật thông tin payment
                    payment.setOrder(updatedOrder);
                    payment.setAmount(updatedOrder.getTotal());
                    payment.setPaymentMethod("vnpay");
                    payment.setStatus("Đã thanh toán");
                    Payment savedPayment = paymentService.save(payment);
                    
                    if (savedPayment == null) {
                        return ResponseEntity.ok(Map.of(
                            "success", false,
                            "message", "Lỗi khi lưu thông tin thanh toán",
                            "data", Map.of(
                                "redirectUrl", "fashionmobile://payment/vnpay/return/mobile?success=false&error=SAVE_PAYMENT_FAILED"
                            )
                        ));
                    }

                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Thanh toán thành công",
                        "data", Map.of(
                            "order", orderMapper.toDTO(updatedOrder),
                            "redirectUrl", "fashionmobile://payment/vnpay/return/mobile?success=true&orderId=" + updatedOrder.getId()
                        )
                    ));
                }
            }

            // Thanh toán thất bại
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Thanh toán thất bại",
                "data", Map.of(
                    "responseCode", responseCode,
                    "transactionStatus", transactionStatus,
                    "redirectUrl", "fashionmobile://payment/vnpay/return/mobile?success=false&errorCode=" + responseCode
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Lỗi xử lý kết quả thanh toán: " + e.getMessage(),
                "data", Map.of(
                    "redirectUrl", "fashionmobile://payment/vnpay/return/mobile?success=false&error=" + e.getMessage()
                )
            ));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Order order = orderOpt.get();
            order.setStatus(status);
            Order updatedOrder = orderService.update(order);
            return ResponseEntity.ok(orderMapper.toDTO(updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating order status: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/retry-payment")
    public ResponseEntity<?> retryPayment(@PathVariable Integer id) {
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy đơn hàng"));
            }

            Order order = orderOpt.get();
            
            // Kiểm tra trạng thái thanh toán
            if ("Đã thanh toán".equals(order.getPaymentStatus())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Đơn hàng này đã được thanh toán"));
            }

            // Kiểm tra trạng thái đơn hàng
            if (!"Đang xử lý".equals(order.getStatus())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chỉ có thể thử lại thanh toán cho đơn hàng đang xử lý"));
            }

            // Tạo payment mới
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotal());
            payment.setPaymentMethod("vnpay");
            payment.setStatus("Chờ thanh toán");
            
            // Lưu payment
            Payment savedPayment = paymentService.save(payment);

            // Cập nhật trạng thái đơn hàng
            order.setPaymentStatus("Chờ thanh toán");
            // Set expired_at = now + 24h
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = now.plusHours(24);
            order.setExpiredAt(Date.from(expired.atZone(ZoneId.systemDefault()).toInstant()));
            orderService.update(order);

            // Tạo URL thanh toán mới
            String paymentUrl = vnPayService.createPaymentUrl(
                id.longValue(), 
                order.getTotal().longValue()
            );

            // Trả về response
            return ResponseEntity.ok(Map.of(
                "message", "Tạo URL thanh toán mới thành công",
                "paymentUrl", paymentUrl,
                "orderId", id,
                "amount", order.getTotal(),
                "transactionCode", savedPayment.getTransactionCode(),
                "expiredAt", order.getExpiredAt()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi xử lý thanh toán lại: " + e.getMessage()));
        }
    }
}
