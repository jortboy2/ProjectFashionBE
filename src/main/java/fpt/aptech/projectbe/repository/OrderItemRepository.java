package fpt.aptech.projectbe.repository;

import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
} 