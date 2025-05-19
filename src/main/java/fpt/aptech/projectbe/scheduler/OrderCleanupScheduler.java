package fpt.aptech.projectbe.scheduler;

import fpt.aptech.projectbe.entites.Order;
import fpt.aptech.projectbe.entites.OrderItem;
import fpt.aptech.projectbe.entites.ProductSize;
import fpt.aptech.projectbe.service.OrderItemService;
import fpt.aptech.projectbe.service.OrderService;
import fpt.aptech.projectbe.service.ProductSizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class OrderCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderCleanupScheduler.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductSizeService productSizeService;

    // Chạy mỗi 5 phút
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredOrders() {
        logger.info("Bắt đầu kiểm tra đơn hàng hết hạn...");
        
        try {
            // Lấy danh sách đơn hàng có payment_status = "Chờ thanh toán" và đã hết hạn
            List<Order> expiredOrders = orderService.findByPaymentStatusAndExpiredAtBefore("Chờ thanh toán", new Date());
            
            if (expiredOrders.isEmpty()) {
                logger.info("Không có đơn hàng nào hết hạn");
                return;
            }

            logger.info("Tìm thấy {} đơn hàng hết hạn", expiredOrders.size());
            
            // Cập nhật trạng thái các đơn hàng hết hạn và hoàn trả số lượng sản phẩm
            for (Order order : expiredOrders) {
                // Lấy danh sách order items
                List<OrderItem> orderItems = orderItemService.findByOrder(order);
                
                // Hoàn trả số lượng sản phẩm
                for (OrderItem item : orderItems) {
                    ProductSize productSize = productSizeService.findByProductAndSize(
                        item.getProduct(), 
                        item.getSize()
                    );
                    
                    if (productSize != null) {
                        // Tăng số lượng sản phẩm lên
                        productSize.setStock(productSize.getStock() + item.getQuantity());
                        productSizeService.save(productSize);
                        logger.info("Đã hoàn trả {} sản phẩm {} size {}",
                            item.getQuantity(),
                            item.getProduct().getName(),
                            item.getSize().getName());
                    }
                }
                
                // Cập nhật trạng thái đơn hàng
                order.setStatus("Đã hủy");
                order.setPaymentStatus("Đã hủy");
                orderService.update(order);
                logger.info("Đã hủy đơn hàng: {}", order.getOrderCode());
            }
            
            logger.info("Hoàn thành việc hủy đơn hàng hết hạn và hoàn trả số lượng sản phẩm");
        } catch (Exception e) {
            logger.error("Lỗi khi hủy đơn hàng hết hạn: {}", e.getMessage());
        }
    }
} 