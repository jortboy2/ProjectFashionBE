package fpt.aptech.projectbe.service.impl;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.User;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.repository.OrderRepository;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.OrderItemService;
import fpt.aptech.projectbe.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductSizeService productSizeService;

    // 1. Lấy tất cả đơn hàng
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // 2. Tìm đơn hàng theo ID
    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    // 3. Lưu đơn hàng mới
    @Override
    @Transactional
    public Order save(Order order) {
        // Set default status if not provided
        if (order.getStatus() == null) {
            order.setStatus("pending");
        }
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("unpaid");
        }

        // Save the order to get the ID
        Order savedOrder = orderRepository.save(order);

        // Set the order reference for each order item
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(savedOrder);
            }
        }

        // If the order is pending, reduce stock for the ordered items
        if ("pending".equals(savedOrder.getStatus())) {
            for (OrderItem item : savedOrder.getOrderItems()) {
                ProductSize productSize = productSizeService.findByProductIdAndSizeId(
                        item.getProduct().getId(),
                        item.getSize().getId()
                );

                if (productSize == null) {
                    throw new RuntimeException("Không tìm thấy thông tin kích thước sản phẩm cho sản phẩm: "
                            + item.getProduct().getName() + " và kích thước: " + item.getSize().getName());
                }

                int currentStock = productSize.getStock();
                int newStock = currentStock - item.getQuantity();

                if (newStock < 0) {
                    throw new RuntimeException("Không đủ hàng trong kho cho sản phẩm: "
                            + item.getProduct().getName() + " (Còn " + currentStock + " sản phẩm, yêu cầu " + item.getQuantity() + ")");
                }

                // Update the stock for the product size
                productSize.setStock(newStock);

                // Save the updated product size
                productSizeService.save(productSize);
            }
        }

        return savedOrder;
    }

    // 4. Xoá đơn hàng theo ID
    @Override
    @Transactional
    public void deleteById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Restore stock when deleting order
        for (OrderItem item : order.getOrderItems()) {
            ProductSize productSize = productSizeService.findByProductIdAndSizeId(
                item.getProduct().getId(),
                item.getSize().getId()
            );

            if (productSize != null) {
                int currentStock = productSize.getStock();
                productSize.setStock(currentStock + item.getQuantity());
                productSizeService.save(productSize);
            }
        }

        orderRepository.deleteById(id);
    }

    // 5. Cập nhật đơn hàng
    @Override
    @Transactional
    public Order update(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // If order is being cancelled, restore stock
        if ("cancelled".equals(order.getStatus()) && !"cancelled".equals(existingOrder.getStatus())) {
            for (OrderItem item : existingOrder.getOrderItems()) {
                ProductSize productSize = productSizeService.findByProductIdAndSizeId(
                    item.getProduct().getId(),
                    item.getSize().getId()
                );

                if (productSize != null) {
                    int currentStock = productSize.getStock();
                    productSize.setStock(currentStock + item.getQuantity());
                    productSizeService.save(productSize);
                }
            }
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

    @Override
    public List<Order> findByUserid(int userid) {
        return orderRepository.findByUserid(userid);
    }
}
