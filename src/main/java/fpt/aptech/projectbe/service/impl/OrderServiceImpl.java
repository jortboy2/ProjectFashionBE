package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.repository.OrderRepository;
import fpt.aptech.projectbe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Lấy tất cả đơn hàng
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // 2. Tìm đơn hàng theo ID
    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    // 3. Lưu đơn hàng mới
    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    // 4. Xoá đơn hàng theo ID
    @Override
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    // 5. Cập nhật đơn hàng
    @Override
    public Order update(Order order) {
        if (order.getId() == null || !orderRepository.existsById(order.getId())) {
            throw new IllegalArgumentException("Order ID không tồn tại, không thể cập nhật");
        }
        return orderRepository.save(order);
    }
    // 6. Tìm đơn hàng theo người dùng
    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // 7. Tìm đơn hàng theo trạng thái
    @Override
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    // 8. Tìm đơn hàng theo trạng thái thanh toán
    @Override
    public List<Order> findByPaymentStatus(String paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }
}
