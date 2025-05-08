package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(String status);
    List<Order> findByPaymentStatus(String paymentStatus);
} 