package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.Product;
import fpt.aptech.projectbe.repository.OrderItemRepository;
import fpt.aptech.projectbe.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    @Override
    public Optional<OrderItem> findById(Integer id) {
        return orderItemRepository.findById(id);
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public void deleteById(Integer id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public OrderItem update(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> findByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    @Override
    public List<OrderItem> findByProduct(Product product) {
        return orderItemRepository.findByProduct(product);
    }
} 