package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Payment;
import fpt.aptech.projectbe.entites.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrder(Order order);
    List<Payment> findByStatus(String status);
    List<Payment> findByPaymentMethod(String paymentMethod);
    List<Payment> findByOrderId(Integer orderId);
} 