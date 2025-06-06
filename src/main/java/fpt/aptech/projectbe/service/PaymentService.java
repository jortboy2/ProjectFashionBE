package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Payment;
import fpt.aptech.projectbe.entites.Order;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> findAll();
    Optional<Payment> findById(Integer id);
    Payment save(Payment payment);
    Payment update(Payment payment);
    void deleteById(Integer id);
    List<Payment> findByOrder(Order order);
    List<Payment> findByStatus(String status);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByOrderId(Integer orderId);
} 