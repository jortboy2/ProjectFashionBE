package fpt.aptech.projectbe.service;

import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.Product;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    List<OrderItem> findAll();
    Optional<OrderItem> findById(Integer id);
    OrderItem save(OrderItem orderItem);
    void deleteById(Integer id);
    OrderItem update(OrderItem orderItem);
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
} 