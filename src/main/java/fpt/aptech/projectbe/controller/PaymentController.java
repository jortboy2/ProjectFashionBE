package fpt.aptech.projectbe.controller;

import fpt.aptech.projectbe.dto.PaymentDTO;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.Payment;
import fpt.aptech.projectbe.mapper.OrderMapper;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        List<PaymentDTO> paymentDTOs = payments.stream()
            .map(orderMapper::toPaymentDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Integer id) {
        return paymentService.findById(id)
            .map(orderMapper::toPaymentDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Integer orderId) {
        if (orderService.findById(orderId).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Payment> payments = paymentService.findByOrderId(orderId);
        List<PaymentDTO> paymentDTOs = payments.stream()
            .map(orderMapper::toPaymentDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            Order order = orderService.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

            if (order.getTotal().compareTo(paymentDTO.getAmount()) > 0) {
                return ResponseEntity.badRequest().build();
            }

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
            payment.setStatus(paymentDTO.getStatus());

            Payment savedPayment = paymentService.save(payment);
            
            // Update order payment status
            order.setPaymentStatus("paid");
            orderService.update(order);

            return ResponseEntity.ok(orderMapper.toPaymentDTO(savedPayment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Integer id, @RequestBody PaymentDTO paymentDTO) {
        return paymentService.findById(id)
            .map(existingPayment -> {
                existingPayment.setAmount(paymentDTO.getAmount());
                existingPayment.setPaymentMethod(paymentDTO.getPaymentMethod());
                existingPayment.setStatus(paymentDTO.getStatus());
                
                Payment updatedPayment = paymentService.update(existingPayment);
                return ResponseEntity.ok(orderMapper.toPaymentDTO(updatedPayment));
            })
            .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completePayment(@PathVariable Integer id) {
        try {
            Optional<Payment> paymentOpt = paymentService.findById(id);
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy payment với ID: " + id));
            }
            
            Payment payment = paymentOpt.get();
            payment.setStatus("Đã thanh toán");
            
            // Update order payment status
            Order order = payment.getOrder();
            order.setPaymentStatus("Đã thanh toán");
            orderService.update(order);
            
            Payment updatedPayment = paymentService.update(payment);
            return ResponseEntity.ok(orderMapper.toPaymentDTO(updatedPayment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        try {
            paymentService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
