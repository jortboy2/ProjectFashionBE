package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(String status);
    List<Order> findByPaymentStatus(String paymentStatus);
    Optional<Order> findByOrderCode(String orderCode);
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findByUserid(@Param("userId") int userId);
    List<Order> findByPaymentStatusAndExpiredAtBefore(String paymentStatus, Date expiredAt);
    List<Order> findByPaymentMethod(String paymentMethod);
} 