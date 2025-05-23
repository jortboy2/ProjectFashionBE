package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();
    Optional<Order> findById(Integer id);
    Order save(Order order);
    void deleteById(Integer id);
    Order update(Order order);
    List<Order> findByUser(User user);
    List<Order> findByStatus(String status);
    List<Order> findByPaymentStatus(String paymentStatus);
    List<Order> findByUserid(int userid);
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findByPaymentStatusAndExpiredAtBefore(String paymentStatus, Date expiredAt);
    List<Order> findByPaymentMethod(String paymentMethod);
} 